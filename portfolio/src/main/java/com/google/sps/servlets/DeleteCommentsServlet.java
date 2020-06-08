package com.google.sps.servlets;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.sps.data.Comment;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/** Servlet that deletes comments */
@WebServlet("/delete-data")
public class DeleteCommentsServlet extends HttpServlet {
   private static final String ENTITY_KEY = "Comment";
   private static final String ENTITY_TEXT = "text";
   private static final String ENTITY_TIMESTAMP = "timestamp";

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    Query query = new Query(ENTITY_KEY);
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    PreparedQuery results = datastore.prepare(query);

    for ( Entity entity : results.asIterable()){
      long id = entity.getKey().getId();
      Key commentKey = KeyFactory.createKey(ENTITY_KEY, id);
      datastore.delete(commentKey);
    }
  }
}
