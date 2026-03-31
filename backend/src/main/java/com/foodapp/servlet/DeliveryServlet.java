package com.foodapp.servlet;

import com.foodapp.dao.RestaurantDAO;
import com.foodapp.model.Restaurant;
import com.foodapp.util.DistanceCalculator;
import com.foodapp.util.Logger;
import com.google.gson.Gson;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class DeliveryServlet extends HttpServlet {
    private RestaurantDAO restaurantDAO = new RestaurantDAO();
    private Gson gson = new Gson();
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        String pathInfo = request.getPathInfo();
        Logger.logAPICall("POST", "/api/delivery" + (pathInfo != null ? pathInfo : ""));
        
        try {
            if ("/quote".equals(pathInfo)) {
                handleDeliveryQuote(request, response);
            } else {
                sendError(response, 404, "Endpoint not found");
            }
        } catch (Exception e) {
            Logger.logError("Error in DeliveryServlet", e);
            sendError(response, 500, "Internal server error: " + e.getMessage());
        }
    }
    
    private void handleDeliveryQuote(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        try {
            Map<String, Object> requestData = gson.fromJson(request.getReader(), Map.class);
            
            double userLat = ((Number) requestData.get("userLat")).doubleValue();
            double userLng = ((Number) requestData.get("userLng")).doubleValue();
            int restaurantId = ((Number) requestData.get("restaurantId")).intValue();
            
            Restaurant restaurant = restaurantDAO.getRestaurantById(restaurantId);
            
            if (restaurant == null) {
                sendError(response, 404, "Restaurant not found");
                return;
            }
            
            double[] distanceAndFee = DistanceCalculator.calculateDistanceAndFee(
                userLat, userLng, 
                restaurant.getLatitude(), restaurant.getLongitude()
            );
            
            double distanceKm = Math.round(distanceAndFee[0] * 100.0) / 100.0;
            double deliveryFee = distanceAndFee[1];
            
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("success", true);
            responseData.put("distanceKm", distanceKm);
            responseData.put("deliveryFee", deliveryFee);
            responseData.put("restaurantId", restaurantId);
            responseData.put("restaurantName", restaurant.getName());
            
            Logger.log("Delivery quote: " + distanceKm + "km, Fee: ₹" + deliveryFee);
            
            response.getWriter().write(gson.toJson(responseData));
            
        } catch (Exception e) {
            Logger.logError("Delivery quote error", e);
            sendError(response, 500, "Failed to calculate delivery quote: " + e.getMessage());
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
