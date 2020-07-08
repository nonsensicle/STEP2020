package com.google.sps.servlets;

import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;

/** Servlet that deletes comments from the database. */
@WebServlet("/delete-data")
public class DeleteDataServlet extends HttpServlet{

  /** Delete all entries from the database and return empty response. */
  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    // Query for entities with kind Comment.
    Query query = new Query("Comment");
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    PreparedQuery results = datastore.prepare(query);

    // Retrieve each entry's unique stored ID and delete using that as a key.
    for (Entity entity: results.asIterable()) {
      long id = entity.getKey().getId();
      Key commentEntityKey = KeyFactory.createKey("Comment", id);
      datastore.delete(commentEntityKey);
    }

    response.sendRedirect("contact.html");
  }
}
