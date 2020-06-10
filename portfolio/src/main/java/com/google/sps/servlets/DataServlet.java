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
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
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
  private static final String ENTITY_USERNAME = "username";
  private static final String ENTITY_EMAIL = "userEmail";
  private static final String USERNAMEID = "user-name";
  private static final String USERCOMMENTID = "user-comment";
  private static final String USEREMAILID = "user-email";

  // Responsible for listing comments  
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    Query query = new Query(ENTITY_KEY).addSort(ENTITY_TIMESTAMP, SortDirection.DESCENDING);
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    PreparedQuery results = datastore.prepare(query);

    List<Comment> comments = new ArrayList<>();

    for ( Entity entity : results.asIterable()){
      long id = entity.getKey().getId();
      String username = (String) entity.getProperty(ENTITY_USERNAME);
      String text = (String) entity.getProperty(ENTITY_TEXT);
      long timestamp = (long) entity.getProperty(ENTITY_TIMESTAMP);
      String userEmail = (String) entity.getProperty(ENTITY_EMAIL);

      Comment comment = new Comment(id, username, text, timestamp, userEmail);
      comments.add(comment);
    }

    Gson gson = new Gson();
    String jsonComments = gson.toJson(comments);
    response.setContentType("application/json");
    response.getWriter().println(jsonComments);
  }

  /** POST method for getting user comments from homepage and storing them */
  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String userComment = getUserInfo(request, USERCOMMENTID);
    String userName = getUserInfo(request, USERNAMEID);
    String userEmail = getUserInfo(request, USEREMAILID);
    long timestamp = System.currentTimeMillis();

    Entity commentEntity = new Entity(ENTITY_KEY);
    commentEntity.setProperty(ENTITY_USERNAME, userName);
    commentEntity.setProperty(ENTITY_TEXT, userComment);
    commentEntity.setProperty(ENTITY_TIMESTAMP, timestamp);
    commentEntity.setProperty(ENTITY_EMAIL, userEmail);
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    datastore.put(commentEntity);
    
    response.sendRedirect("/index.html");
  }

  /** returns user information based on what propertyId given */
  private String getUserInfo(HttpServletRequest request, String propertyId) {
    if ( propertyId==USEREMAILID ){
        UserService userService = UserServiceFactory.getUserService();
        return userService.getCurrentUser().getEmail();
    }
    String property = request.getParameter(propertyId);
    if (property.isEmpty()) {
      System.err.println("Input box empty! Please ensure you type in your username and comment");
      return "error";
    }
    return property;
  }
}
