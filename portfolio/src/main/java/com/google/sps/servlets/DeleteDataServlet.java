package com.google.sps.servlets;

import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.google.appengine.api.datastore.Entity;
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
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    String queryString = "Comment";
    Query query = new Query(queryString);
    query.setKeysOnly();
    PreparedQuery preparedQuery = datastore.prepare(query);        
    for (Entity entity: preparedQuery.asIterable()) {
      datastore.delete(entity.getKey());
    }
  }
}