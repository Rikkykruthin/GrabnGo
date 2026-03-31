package com.foodapp.servlet;

import com.foodapp.dao.RestaurantDAO;
import com.foodapp.dao.MenuDAO;
import com.foodapp.model.Restaurant;
import com.foodapp.model.MenuItem;
import com.foodapp.util.Logger;
import com.google.gson.Gson;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RestaurantServlet extends HttpServlet {
    private RestaurantDAO restaurantDAO = new RestaurantDAO();
    private MenuDAO menuDAO = new MenuDAO();
    private Gson gson = new Gson();
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        String pathInfo = request.getPathInfo();
        Logger.logAPICall("GET", "/api/restaurants" + (pathInfo != null ? pathInfo : ""));
        
        try {
            if (pathInfo == null || "/".equals(pathInfo)) {
                handleGetAllRestaurants(request, response);
            } else if (pathInfo.matches("/\\d+")) {
                handleGetRestaurantById(request, response, pathInfo);
            } else if (pathInfo.matches("/\\d+/menu")) {
                handleGetRestaurantMenu(request, response, pathInfo);
            } else {
                sendError(response, 404, "Endpoint not found");
            }
        } catch (Exception e) {
            Logger.logError("Error in RestaurantServlet", e);
            sendError(response, 500, "Failed to process request: " + e.getMessage());
        }
    }
    
    private void handleGetAllRestaurants(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        try {
            List<Restaurant> restaurants = restaurantDAO.getAllRestaurants();
            
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("success", true);
            responseData.put("restaurants", restaurants);
            
            response.getWriter().write(gson.toJson(responseData));
            
        } catch (Exception e) {
            Logger.logError("Error fetching restaurants", e);
            sendError(response, 500, "Failed to fetch restaurants: " + e.getMessage());
        }
    }
    
    private void handleGetRestaurantById(HttpServletRequest request, HttpServletResponse response, String pathInfo) 
            throws IOException {
        try {
            int restaurantId = Integer.parseInt(pathInfo.substring(1));
            Restaurant restaurant = restaurantDAO.getRestaurantById(restaurantId);
            
            if (restaurant == null) {
                sendError(response, 404, "Restaurant not found");
                return;
            }
            
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("success", true);
            responseData.put("restaurant", restaurant);
            
            response.getWriter().write(gson.toJson(responseData));
            
        } catch (NumberFormatException e) {
            sendError(response, 400, "Invalid restaurant ID");
        } catch (Exception e) {
            Logger.logError("Error fetching restaurant", e);
            sendError(response, 500, "Failed to fetch restaurant: " + e.getMessage());
        }
    }
    
    private void handleGetRestaurantMenu(HttpServletRequest request, HttpServletResponse response, String pathInfo) 
            throws IOException {
        try {
            String[] parts = pathInfo.split("/");
            int restaurantId = Integer.parseInt(parts[1]);
            
            List<MenuItem> menuItems = menuDAO.getMenuByRestaurantId(restaurantId);
            
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("success", true);
            responseData.put("menuItems", menuItems);
            responseData.put("restaurantId", restaurantId);
            
            response.getWriter().write(gson.toJson(responseData));
            
        } catch (NumberFormatException e) {
            sendError(response, 400, "Invalid restaurant ID");
        } catch (Exception e) {
            Logger.logError("Error fetching menu", e);
            sendError(response, 500, "Failed to fetch menu: " + e.getMessage());
        }
    }
    
    private void sendError(HttpServletResponse response, int status, String message) throws IOException {
        response.setStatus(status);
        Map<String, Object> error = new HashMap<>();
        error.put("success", false);
        error.put("error", message);
        response.getWriter().write(gson.toJson(error));
    }
}
