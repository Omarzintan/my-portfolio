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

/** Servlet that deletes comments */
@WebServlet("/delete-data")
public class DeleteCommentServlet extends HttpServlet {
   private static final String entityKey = "Comment";
   private static final String entityText = "text";
   private static final String entityTimeStamp = "timestamp";

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    Query query = new Query(entityKey);
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    PreparedQuery results = datastore.prepare(query);

    List<Comment> comments = new ArrayList<>();

    for ( Entity entity : results.asIterable()){
      datastore.delete(entity.getKey());
    }

    Gson gson = new Gson();
    String jsonComments = gson.toJson(results);
    response.setContentType("application/json");
    response.getWriter().println(jsonComments);
  }
}
