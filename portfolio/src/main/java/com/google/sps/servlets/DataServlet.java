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
import java.util.ArrayList;
import com.google.gson.Gson;



/** Servlet that returns some example content. TODO: modify this file to handle comments data */
@WebServlet("/data")
public class DataServlet extends HttpServlet {
    
  // hard-coded messages for testing
  public ArrayList<String> comments = new ArrayList<String>();

  public DataServlet() {
    comments.add("comment1");
    comments.add("comment2");
    comments.add("comment3");
  }
  
 

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String commentsString = convertToJsonWithGson(comments);
    // response.setContentType("text/html;");
    // response.getWriter().println("Hello Zintan!");
    response.getWriter().println(commentsString);
  }

  // Converts messages to JSON format using GSON
  private String convertToJsonWithGson(ArrayList<String> messages){
      Gson gson = new Gson();
      String jsonMessages = gson.toJson(messages);
      return jsonMessages;
  }
}
