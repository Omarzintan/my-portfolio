package com.google.sps.servlets;

import com.google.appengine.api.datastore.*;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.ArrayList;
import com.google.gson.Gson;

@WebServlet("/login")
public class UserLoginServlet extends HttpServlet {

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    response.setContentType("application/json");
    PrintWriter out = response.getWriter();
    UserService userService = UserServiceFactory.getUserService();
    Gson gson = new Gson();
    List<String> responseList = new ArrayList<>(5);
    String loginStatus;
    String loginUrl = userService.createLoginURL("/");
    String logoutUrl = userService.createLogoutURL("/");
    String userEmail = "";
    String userNickname = "";

    if (!userService.isUserLoggedIn()) {
      loginStatus = "0";
    }
    else {
      loginStatus = "1";
      userEmail = userService.getCurrentUser().getEmail();
      userNickname = getUserNickname(userService.getCurrentUser().getUserId());
      if (userNickname == null) {
        response.sendRedirect("/user-nickname");
        return;
      }
    }
    responseList.add(0, loginStatus);
    responseList.add(1, loginUrl);
    responseList.add(2, logoutUrl);
    responseList.add(3, userEmail);
    responseList.add(4, userNickname);
    out.println(gson.toJson(responseList));
  }

  /**
   * Returns the nickname of the user with id, or null if the user has not set a nickname.
   */
  private String getUserNickname(String id) {
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    Query query =
        new Query("UserInfo")
            .setFilter(new Query.FilterPredicate("id", Query.FilterOperator.EQUAL, id));
    PreparedQuery results = datastore.prepare(query);
    Entity entity = results.asSingleEntity();
    if (entity == null) {
      return null;
    }
    String nickname = (String) entity.getProperty("user-nickname");
    return nickname;
  }
}
