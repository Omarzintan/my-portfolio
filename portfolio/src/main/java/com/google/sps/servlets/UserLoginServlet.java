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
    List<String> responseList = new ArrayList<>(3);
    String loginStatus;
    String loginUrl = userService.createLoginURL("/");
    String logoutUrl = userService.createLogoutURL("/");
    

    // If user is not logged in
    if (!userService.isUserLoggedIn()) {
        loginStatus="0";
    }
    else {
        // User is logged in
        loginStatus="1";
    }
    responseList.add(0, loginStatus);
    responseList.add(1, loginUrl);
    responseList.add(2, logoutUrl);
    out.println(gson.toJson(responseList));
  }
}
