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

/** Servlet that returns some example content. TODO: modify this file to handle comments data */
@WebServlet("/data")
public class DataServlet extends HttpServlet {

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    // Query for Comment Entities in this instance of Datastore.
    Query query = new Query("Comment").addSort("date", SortDirection.DESCENDING);
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    PreparedQuery results = datastore.prepare(query);
    
    // Loop through found Comment Entities and add then to an arraylist of comments.
    ArrayList<Comment> comments = new ArrayList<Comment>();
    for (Entity entity: results.asIterable()) {
      long id = entity.getKey().getId();
      String fname = (String) entity.getProperty("fname");
      String surname = (String) entity.getProperty("surname");
      String email = (String) entity.getProperty("email");
      Date date = (Date) entity.getProperty("date");
      String message = (String) entity.getProperty("message");

      Comment comment = new Comment(fname, surname, email, date, message, id);
      comments.add(comment);
    }

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
    commentEntity.setProperty("fname", fname);
    commentEntity.setProperty("surname", surname);
    commentEntity.setProperty("email", email);
    commentEntity.setProperty("date", new Date());
    commentEntity.setProperty("message", subject);

    // Make an instance of DatastoreService and put comment entity in.
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    datastore.put(commentEntity);

    // Redirect back to the HTML page.
    response.sendRedirect("/contact.html");
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
