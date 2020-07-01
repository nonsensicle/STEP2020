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
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;

/** Servlet that returns some example content. TODO: modify this file to handle comments data */
@WebServlet("/data")
public class DataServlet extends HttpServlet {
 
  // Holds all the comments. 
  private ArrayList<Comment> comments = new ArrayList<Comment>();

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String jsonString = new Gson().toJson(comments);  // Gson method takes private vars in each comment
    response.setContentType("application/json;");
    response.getWriter().println(jsonString);
  }

  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException  {
    // Get HTML form input and make a Comment object with the data.
    String fname = request.getParameter("first");
    String surname = request.getParameter("last");
    String email = request.getParameter("mail");
    String subject = request.getParameter("subject");
    Comment currComment = new Comment(fname, surname, email, subject);

    comments.add(currComment);

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
