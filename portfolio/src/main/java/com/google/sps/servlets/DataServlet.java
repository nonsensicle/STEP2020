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
import com.google.gson.*;
import com.google.gson.annotations.*;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.*;

/** Servlet that returns comments.*/
@WebServlet("/data")
public class DataServlet extends HttpServlet {

  private final String FNAME = "fname";
  private final String SURNAME = "surname";
  private final String EMAIL = "email";
  private final String DATE = "date";
  private final String MESSAGE = "message";
  private final int DEFAULT_NUM_COMMENTS = 10;

  // For logs.
  private final Logger logger = Logger.getLogger(DataServlet.class.getName());

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
      response.getWriter().println("Please enter an integer in range.");
      return;
    }

    // Loop through found Comment Entities and add then to an arraylist of comments.
    // Limit amount of comments displayed to user's choice.
    ArrayList<Comment> comments = new ArrayList<Comment>();
    int counter = 0;
    for (Entity entity: results.asIterable()) {
      if (counter < maxComments) {
        counter ++;
        long id = entity.getKey().getId();
        String fname = (String) entity.getProperty(FNAME);
        String surname = (String) entity.getProperty(SURNAME);
        String email = (String) entity.getProperty(EMAIL);
        Date date = (Date) entity.getProperty(DATE);
        String message = (String) entity.getProperty(MESSAGE);

        Comment comment = new Comment(fname, surname, email, date, message, id);
        comments.add(comment);
      }
      else { break;}
    }

    // Send back a JSON object.
    String jsonString = new Gson().toJson(comments);  // Gson method takes private vars in each comment
    response.setContentType("application/json;");
    response.getWriter().println(jsonString);
  }

  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException  {
    // Get HTML form input and make a Comment Entity with the data.
    String fname = request.getParameter("first");
    String surname = request.getParameter("last");
    String email = request.getParameter("mail");
    String subject = request.getParameter("subject");

    Entity commentEntity = new Entity("Comment");
    commentEntity.setProperty(FNAME, fname);
    commentEntity.setProperty(SURNAME, surname);
    commentEntity.setProperty(EMAIL, email);
    commentEntity.setProperty(DATE, new Date());
    commentEntity.setProperty(MESSAGE, subject);

    // Make an instance of DatastoreService and put comment entity in.
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    datastore.put(commentEntity);

    // Redirect back to the HTML page.
    response.sendRedirect("/contact.html");
  }

  /** Return the number of comments user wants shown, or default.*/
  private int getNumComments(HttpServletRequest request) {
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

    return numComments;
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
