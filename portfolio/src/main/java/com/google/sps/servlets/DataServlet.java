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

import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.ArrayList;
import com.google.gson.Gson;



/** Servlet that returns some example content. TODO: modify this file to handle comments data */
@WebServlet("/data")
public class DataServlet extends HttpServlet {
    
  // hard-coded messages for testing
  public List<String> comments = new ArrayList<String>();

  public DataServlet() {}
  
 

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String commentsString = convertToJsonWithGson(comments);
    response.getWriter().println(commentsString);
  }

  /**POST method for getting user comments from homepage */
  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String userComment = getUserComment(request);
    comments.add(userComment); 
    response.sendRedirect("/index.html");
  }

  // Converts messages to JSON format using GSON
  private String convertToJsonWithGson(List<String> messages){
    Gson gson = new Gson();
    String jsonMessages = gson.toJson(messages);
    return jsonMessages;
  }

  private String getUserComment(HttpServletRequest request){
    String comment = request.getParameter("user-comment");
    if (comment == "") {
      System.err.println("Comment box empty! Please type in your comment");
      return "error";
    }
    return comment;
  }
}
