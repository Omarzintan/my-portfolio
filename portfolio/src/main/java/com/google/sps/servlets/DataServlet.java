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

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.sps.data.Comment;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.ArrayList;
import com.google.gson.Gson;



/** Servlet that handles comments. */
@WebServlet("/data")
public class DataServlet extends HttpServlet {
  private static final String ENTITY_KEY = "Comment";
  private static final String ENTITY_TIMESTAMP = "timestamp";
  private static final String ENTITY_TEXT = "text";

  // Responsible for listing comments  
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    Query query = new Query(ENTITY_KEY).addSort(ENTITY_TIMESTAMP, SortDirection.DESCENDING);
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    PreparedQuery results = datastore.prepare(query);

    List<Comment> comments = new ArrayList<>();

    for ( Entity entity : results.asIterable()){
      long id = entity.getKey().getId();
      String text = (String) entity.getProperty(ENTITY_TEXT);
      long timestamp = (long) entity.getProperty(ENTITY_TIMESTAMP);

      Comment comment = new Comment(id, text, timestamp);
      comments.add(comment);
    }

    Gson gson = new Gson();
    String jsonComments = gson.toJson(comments);
    response.setContentType("application/json");
    response.getWriter().println(jsonComments);
  }

  /** POST method for getting user comments from homepage */
  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String userComment = getUserComment(request);
    long timestamp = System.currentTimeMillis();

    Entity commentEntity = new Entity(ENTITY_KEY);
    commentEntity.setProperty(ENTITY_TEXT, userComment);
    commentEntity.setProperty(ENTITY_TIMESTAMP, timestamp);
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    datastore.put(commentEntity);
    
    response.sendRedirect("/index.html");
  }

  // Converts messages to JSON format using GSON
  private String convertToJsonWithGson(List<String> messages){
    Gson gson = new Gson();
    String jsonMessages = gson.toJson(messages);
    return jsonMessages;
  }

  private String getUserComment(HttpServletRequest request){
    String commentId = "user-comment";
    String comment = request.getParameter(commentId);
    if (comment.isEmpty()) {
      System.err.println("Comment box empty! Please type in your comment");
      return "error";
    }
    return comment;
  }
}
