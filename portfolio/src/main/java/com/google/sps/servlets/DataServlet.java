// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps.servlets;

import com.google.sps.data.Comment;
import com.google.appengine.api.blobstore.BlobInfo;
import com.google.appengine.api.blobstore.BlobInfoFactory;
import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.appengine.api.images.ImagesService;
import com.google.appengine.api.images.ImagesServiceFactory;
import com.google.appengine.api.images.ServingUrlOptions;
import com.google.api.gax.rpc.ApiException;
import com.google.gson.*;
import com.google.gson.annotations.*;
import com.google.cloud.vision.v1.AnnotateImageRequest;
import com.google.cloud.vision.v1.AnnotateImageResponse;
import com.google.cloud.vision.v1.BatchAnnotateImagesResponse;
import com.google.cloud.vision.v1.BoundingPoly;
import com.google.cloud.vision.v1.EntityAnnotation;
import com.google.cloud.vision.v1.Feature;
import com.google.cloud.vision.v1.Image;
import com.google.cloud.vision.v1.ImageAnnotatorClient;
import com.google.cloud.vision.v1.LocalizedObjectAnnotation;
import com.google.cloud.vision.v1.NormalizedVertex;
import com.google.protobuf.ByteString;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.RoundingMode;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.*;
import java.util.List;
import java.util.Map;

/** Servlet that returns comments.*/
@WebServlet("/data")
public class DataServlet extends HttpServlet {

  private final String FNAME = "fname";
  private final String SURNAME = "surname";
  private final String EMAIL = "email";
  private final String DATE = "date";
  private final String MESSAGE = "message";
  private final String IMAGE_KEY = "image-key";
  private final String IMAGE_LABELS = "image-labels";
  private final String LABEL_SCORES = "label-scores";
  private final String OBJECT_NAMES = "obj-names";
  private final String OBJECT_SCORES = "obj-scores";
  private final int DEFAULT_NUM_COMMENTS = 10;
  private final int MAX_COMMENTS = 50;

  // For logs.
  private final Logger logger = Logger.getLogger(DataServlet.class.getName());

  // TODO: refactor (send building of comment to a different function)
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    // Query for Comment Entities in this instance of Datastore.
    Query query = new Query("Comment").addSort("date", SortDirection.DESCENDING);
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    PreparedQuery results = datastore.prepare(query);
    
    // Get max number of comments user wants.
    // If there was some error with user input, return.
    int maxComments = getNumComments(request);
    if (maxComments == -1) {
      response.setContentType("text/html");
      response.getWriter().println("Please enter an integer in the range 1 - " + MAX_COMMENTS + ".");
      return;
    }

    // Create a list of Comments to be returned as JSON.
    ArrayList<Comment> comments = makeCommentList(results, maxComments);

    // Send back a JSON object.
    String jsonString = new Gson().toJson(comments);  // Gson method takes private vars in each comment
    response.setContentType("application/json;");
    response.getWriter().println(jsonString);
  }
  
  /** Helper method for doGet() that builds a list of Comments based on a prepared query and a max number of comments. */
  public ArrayList<Comment> makeCommentList(PreparedQuery commentEntities, int numComments) {

    // Loop through found Comment Entities and add then to an arraylist of comments.
    // Limit amount of comments displayed to user's choice.
    ArrayList<Comment> comments = new ArrayList<Comment>();
    int commentCounter = 0;
    for (Entity entity: commentEntities.asIterable()) {
      if (commentCounter < numComments) {
        commentCounter ++;
        long id = entity.getKey().getId();
        String fname = (String) entity.getProperty(FNAME);
        String surname = (String) entity.getProperty(SURNAME);
        String email = (String) entity.getProperty(EMAIL);
        Date date = (Date) entity.getProperty(DATE);
        String message = (String) entity.getProperty(MESSAGE);
        String imageKey = (String) entity.getProperty(IMAGE_KEY);
        ArrayList<String> labels = (ArrayList) entity.getProperty(IMAGE_LABELS);
        ArrayList<Float> scores = (ArrayList) entity.getProperty(LABEL_SCORES);
        ArrayList<String> objects = (ArrayList) entity.getProperty(OBJECT_NAMES);
        ArrayList<Float> objScores = (ArrayList) entity.getProperty(OBJECT_SCORES);
        
        // If no names were entered, display "Anonymous".
        if ((fname == null && surname == null) || (fname.isEmpty() && surname.isEmpty()) ) {
           fname = "Anonymous";
        }

        Comment comment = new Comment(fname, surname, email, date, message, imageKey, labels, scores, objects, objScores, id);
        comments.add(comment);
      }
      else { break;}
    }
    return comments;
  }

  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException  {
    // Get HTML form input and make a Comment Entity with the data.
    String fname = request.getParameter("first");
    String surname = request.getParameter("last");
    String email = request.getParameter("mail");
    String subject = request.getParameter("subject");

    // Get the blob key from Blobstore (if blob was submitted).
    // "image" is the name of the file input in the comment form.
    BlobKey imageKey = getImageKey(request, "image");
   
    // Use one annotate image request to get the labels associated with and the objects
    // detected in the image uploaded by the user, if there was one.
    // Also, create a string from the image key if there was an image.
    ArrayList<String> labels = new ArrayList<String>();
    ArrayList<Float> labelScores = new ArrayList<Float>();
    ArrayList<String> objects = new ArrayList<String>();
    ArrayList<Float> objScores = new ArrayList<Float>();
    String imKey = "";

    if (imageKey != null) { 
      // Create string from key to store in datastore.
      imKey = imageKey.getKeyString(); 

      byte[] imageBytes = getBlobBytes(imageKey);
      List<LocalizedObjectAnnotation> imageObjects = getImageObjects(imageBytes);
      List<EntityAnnotation> imageLabels = getImageLabels(imageBytes);
      
      // Build the lists of image descriptions and scores.
      if (imageLabels != null ) {
        for (EntityAnnotation label: imageLabels) {
          labels.add(label.getDescription());
          labelScores.add(label.getScore());
        }
      } 
      
      if (imageObjects != null) {
        // Build the lists of image objects, scores, and bounding box vertices.
        for (LocalizedObjectAnnotation object: imageObjects) {
          objects.add(object.getName());
          objScores.add(object.getScore());
        }
      }
    }

    Entity commentEntity = makeCommentEntity(fname, surname, email, subject, imKey, labels, 
        labelScores, objects, objScores);

    // Make an instance of DatastoreService and put comment entity in.
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    datastore.put(commentEntity);

    // Redirect back to the HTML page.
    response.sendRedirect("/contact.html");
  }

  /** doPost() helper method; makes a Comment Entity with given parameters. */
  public Entity makeCommentEntity(String fname, String surname, String email, String subject, String imKey,
      ArrayList<String> labels, ArrayList<Float> labelScores, ArrayList<String> objects, ArrayList<Float> objScores) {
    
    Entity commentEntity = new Entity("Comment");
    commentEntity.setProperty(FNAME, fname);
    commentEntity.setProperty(SURNAME, surname);
    commentEntity.setProperty(EMAIL, email);
    commentEntity.setProperty(DATE, new Date());
    commentEntity.setProperty(MESSAGE, subject);
    commentEntity.setProperty(IMAGE_KEY, imKey);
    
    commentEntity.setProperty(IMAGE_LABELS, labels);  
    commentEntity.setProperty(LABEL_SCORES, labelScores); 
    commentEntity.setProperty(OBJECT_NAMES, objects);
    commentEntity.setProperty(OBJECT_SCORES, objScores);

    return commentEntity;
  }

  /** Return the number of comments user wants shown, or default.*/
  public int getNumComments(HttpServletRequest request) {
    // Get the string input by the user in the num-comments field.
    String numString = request.getParameter("num-comments");

    // Convert input to int.
    // If this cannot be done, return default.
    int numComments;
    try {
      numComments = Integer.parseInt(numString);
    }
    catch (NumberFormatException e) {
      logger.warning("Not a valid integer.");
      return DEFAULT_NUM_COMMENTS;
    }

    // If int out of range (negative or greater than max) return -1.
    if (numComments > MAX_COMMENTS || numComments < 0) {
      return -1;
    }

    return numComments;
  }

  /** Return the key of the uploaded image (null if no upload or if not image). 
   * (Referenced FormHandlerServlet.java in hello-world-fetch.)
   */
  private BlobKey getImageKey(HttpServletRequest request, String formElementID) {
    BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
    // Blobstore maps form element ID to a list of keys of the blobs uploaded by the form.
    Map<String, List<BlobKey>> blobs = blobstoreService.getUploads(request);
    List<BlobKey> blobKeys = blobs.get(formElementID);

    // User submitted form without file selected, so no blobKey.
    if (blobKeys == null || blobKeys.isEmpty()) {
      return null;
    }

    // Form only allowed for one Blob input, so get first blobkey.
    BlobKey blobKey = blobKeys.get(0);
    
    // User submitted form with no file, but an empty key was created--delete it.
    BlobInfo blobInfo = new BlobInfoFactory().loadBlobInfo(blobKey);
    if (blobInfo.getSize() == 0) {
      blobstoreService.delete(blobKey);
      return null;
    }

    // Check to make sure the file uploaded was an image.
    // If the first five letters of the type are not "image," then return null and delete Blob from storage.
    String fileType = blobInfo.getContentType();
    // System.out.println(fileType);
    if (!fileType.substring(0,5).equals("image")) {
      blobstoreService.delete(blobKey);
      System.err.println("File uploaded was not an image");
      return null;
    }
    
    // In previous versions, ImagesServices was used to get a URL pointing to the uploaded img.
    // Here, we simpy return the Blob's key so that it can later be served directly.
    return blobKey;
  }

  /** 
   * Blobstore stores files as binary data; this func retrieves the data stored at imageKey. 
   * (See image-analysis directory example)
   */
  private byte[] getBlobBytes(BlobKey imageKey) throws IOException {
    BlobstoreService bs = BlobstoreServiceFactory.getBlobstoreService();
    ByteArrayOutputStream outputBytes = new ByteArrayOutputStream();

    int fetchSize = BlobstoreService.MAX_BLOB_FETCH_SIZE;
    long currByteIndex = 0;
    int bytesLength = 0;
    do {
      // Fetch a portion of the image bytes from Blobstore.
      byte[] temp = bs.fetchData(imageKey, currByteIndex, currByteIndex + fetchSize - 1); 
      bytesLength = temp.length;
      outputBytes.write(temp);

      currByteIndex += fetchSize;
    } while(bytesLength >= fetchSize);  // If fewer bytes than requested are read, then the end was reached.
    return outputBytes.toByteArray();
  }


  /** 
   * Uses Cloud Vision API to generate a list of objects for the image
   * represented by the binary data stored in the imgBytes param.
   */
  private List<LocalizedObjectAnnotation> getImageObjects(byte[] imageBytes) throws IOException {
    ByteString byteString = ByteString.copyFrom(imageBytes);
    Image image = Image.newBuilder().setContent(byteString).build();

    // Use a Feature to make an annotate-image request; add this request to a list of such.
    Feature feature = Feature.newBuilder().setType(Feature.Type.OBJECT_LOCALIZATION).build();
    AnnotateImageRequest request = 
        AnnotateImageRequest.newBuilder().addFeatures(feature).setImage(image).build();
    List<AnnotateImageRequest> requests = new ArrayList<>();
    requests.add(request);

    // Batch annotate all the image requests in the list (only one, here) and get the responses.
    try (ImageAnnotatorClient client = ImageAnnotatorClient.create()) {
      BatchAnnotateImagesResponse batchResponse = client.batchAnnotateImages(requests);
      AnnotateImageResponse imageResponse = batchResponse.getResponsesList().get(0);
 
      if (imageResponse.hasError()) {
        System.err.println("Error getting img response: " + imageResponse.getError().getMessage());
        return null;
      }
      return imageResponse.getLocalizedObjectAnnotationsList();
    }
    catch (ApiException ae) {
      System.err.println("Error: " + ae);
      return null;
    }
  }

  /** 
   * Uses Cloud Vision API to generate a list of labels for the image
   * represented by the binary data stored in the imgBytes param.
   */
  private List<EntityAnnotation> getImageLabels(byte[] imageBytes) throws IOException {
    ByteString byteString = ByteString.copyFrom(imageBytes);
    Image image = Image.newBuilder().setContent(byteString).build();

    // Use a Feature to make an annotate-image request; add this request to a list of such.
    Feature feature = Feature.newBuilder().setType(Feature.Type.LABEL_DETECTION).build();
    AnnotateImageRequest request = 
        AnnotateImageRequest.newBuilder().addFeatures(feature).setImage(image).build();
    List<AnnotateImageRequest> requests = new ArrayList<>();
    requests.add(request);

    // Batch annotate all the image requests in the list (only one, here) and get the responses.
    try (ImageAnnotatorClient client = ImageAnnotatorClient.create()) {
      BatchAnnotateImagesResponse batchResponse = client.batchAnnotateImages(requests);
      AnnotateImageResponse imageResponse = batchResponse.getResponsesList().get(0);
 
      if (imageResponse.hasError()) {
        System.err.println("Error getting img response: " + imageResponse.getError().getMessage());
        return null;
      }
      return imageResponse.getLabelAnnotationsList();
    }
    catch (ApiException ae) {
      System.err.println("Error: " + ae);
      return null;
    }
  }

  /*
   * Manually build a JSON string upon request (doGet()). Obsolete since Step 3, week 3.
   */ 
  private String convertToJson(ArrayList<String> data) {
    StringBuilder json = new StringBuilder();
    json.append("{");

    // Loop through the messages and add each one under the key "message" + "i".
    for (int i = 0; i < data.size(); i++) {
        json.append("\"" + "message" + i + "\": ");
        json.append("\"" + data.get(i) + "\"");

        // If not on the last message, add a comma for proper JSON parsing.
        if (i != data.size() - 1) {
            json.append(", ");
        }
    }
    json.append("}");

    return json.toString();
  }
}
