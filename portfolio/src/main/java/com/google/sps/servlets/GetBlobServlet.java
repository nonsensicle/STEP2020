package com.google.sps.servlets;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * When a user submits an image via the comment form, Blobstore handles the upload and
 * forwards the POST request to DataServlet. When the fetch() function requests the /get-blob URL,
 * the Blob is served from Blobstore.
 */
@WebServlet("/get-blob")
public class GetBlobServlet extends HttpServlet {

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
    BlobKey blobKey = new BlobKey(request.getParameter("blob-key"));
    blobstoreService.serve(blobKey, response);
  }
}
