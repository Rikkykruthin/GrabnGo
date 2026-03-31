package com.foodapp.servlet;

import com.foodapp.dao.UserDAO;
import com.foodapp.model.User;
import com.foodapp.util.EmailUtil;
import com.foodapp.util.Logger;
import com.google.gson.Gson;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class AuthServlet extends HttpServlet {
    private UserDAO userDAO = new UserDAO();
    private Gson gson = new Gson();
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        String pathInfo = request.getPathInfo();
        Logger.logAPICall("POST", "/api/auth" + (pathInfo != null ? pathInfo : ""));
        
        try {
            if ("/register".equals(pathInfo) || "/signup".equals(pathInfo)) {
                handleRegister(request, response);
            } else if ("/login".equals(pathInfo)) {
                handleLogin(request, response);
            } else if ("/logout".equals(pathInfo)) {
                handleLogout(request, response);
            } else {
                sendError(response, 404, "Endpoint not found");
            }
        } catch (Exception e) {
            Logger.logError("Error in AuthServlet", e);
            sendError(response, 500, "Internal server error: " + e.getMessage());
        }
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        String pathInfo = request.getPathInfo();
        Logger.logAPICall("GET", "/api/auth" + (pathInfo != null ? pathInfo : ""));
        
        try {
            if ("/me".equals(pathInfo)) {
                handleGetCurrentUser(request, response);
            } else {
                sendError(response, 404, "Endpoint not found");
            }
        } catch (Exception e) {
            Logger.logError("Error in AuthServlet", e);
            sendError(response, 500, "Internal server error: " + e.getMessage());
        }
    }
    
    private void handleRegister(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        try {
            User user = gson.fromJson(request.getReader(), User.class);
            
            // Validation
            if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
                sendError(response, 400, "Email is required");
                return;
            }
            if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
                sendError(response, 400, "Password is required");
                return;
            }
            if (user.getName() == null || user.getName().trim().isEmpty()) {
                sendError(response, 400, "Name is required");
                return;
            }
            
            // Check if email exists
            if (userDAO.emailExists(user.getEmail())) {
                sendError(response, 400, "Email already registered");
                return;
            }
            
            // Register user
            User registeredUser = userDAO.registerUser(user);
            
            // Send registration email
            EmailUtil.sendRegistrationEmail(registeredUser.getEmail(), registeredUser.getName());
            
            // Create session
            HttpSession session = request.getSession();
            session.setAttribute("userId", registeredUser.getId());
            session.setAttribute("userEmail", registeredUser.getEmail());
            session.setAttribute("userName", registeredUser.getName());
            
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("success", true);
            responseData.put("message", "Registration successful");
            responseData.put("user", registeredUser);
            
            response.setStatus(201);
            response.getWriter().write(gson.toJson(responseData));
            
        } catch (Exception e) {
            Logger.logError("Registration error", e);
            sendError(response, 500, "Registration failed: " + e.getMessage());
        }
    }
    
    private void handleLogin(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        try {
            Map<String, String> credentials = gson.fromJson(request.getReader(), Map.class);
            String email = credentials.get("email");
            String password = credentials.get("password");
            
            if (email == null || password == null) {
                sendError(response, 400, "Email and password are required");
                return;
            }
            
            User user = userDAO.loginUser(email, password);
            
            if (user == null) {
                sendError(response, 401, "Invalid email or password");
                return;
            }
            
            // Create session
            HttpSession session = request.getSession();
            session.setAttribute("userId", user.getId());
            session.setAttribute("userEmail", user.getEmail());
            session.setAttribute("userName", user.getName());
            
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("success", true);
            responseData.put("message", "Login successful");
            responseData.put("user", user);
            
            response.getWriter().write(gson.toJson(responseData));
            
        } catch (Exception e) {
            Logger.logError("Login error", e);
            sendError(response, 500, "Login failed: " + e.getMessage());
        }
    }
    
    private void handleLogout(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        HttpSession session = request.getSession(false);
        if (session != null) {
            String email = (String) session.getAttribute("userEmail");
            Logger.logUserAction("LOGOUT", email);
            session.invalidate();
        }
        
        Map<String, Object> responseData = new HashMap<>();
        responseData.put("success", true);
        responseData.put("message", "Logout successful");
        
        response.getWriter().write(gson.toJson(responseData));
    }
    
    private void handleGetCurrentUser(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            sendError(response, 401, "Not authenticated");
            return;
        }
        
        User user = new User();
        user.setId((Integer) session.getAttribute("userId"));
        user.setEmail((String) session.getAttribute("userEmail"));
        user.setName((String) session.getAttribute("userName"));
        
        Map<String, Object> responseData = new HashMap<>();
        responseData.put("success", true);
        responseData.put("user", user);
        
        response.getWriter().write(gson.toJson(responseData));
    }
    
    private void sendError(HttpServletResponse response, int status, String message) throws IOException {
        response.setStatus(status);
        Map<String, Object> error = new HashMap<>();
        error.put("success", false);
        error.put("error", message);
        response.getWriter().write(gson.toJson(error));
    }
}
