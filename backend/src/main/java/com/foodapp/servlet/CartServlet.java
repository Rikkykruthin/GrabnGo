package com.foodapp.servlet;

import com.foodapp.dao.RestaurantDAO;
import com.foodapp.model.CartItem;
import com.foodapp.model.Restaurant;
import com.foodapp.util.DistanceCalculator;
import com.foodapp.util.Logger;
import com.google.gson.Gson;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CartServlet extends HttpServlet {
    private RestaurantDAO restaurantDAO = new RestaurantDAO();
    private Gson gson = new Gson();
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        Logger.logAPICall("GET", "/api/cart");
        
        HttpSession session = request.getSession();
        List<CartItem> cart = (List<CartItem>) session.getAttribute("cart");
        
        if (cart == null) {
            cart = new ArrayList<>();
        }
        
        double total = cart.stream().mapToDouble(CartItem::getSubtotal).sum();
        double deliveryFee = 0.0;
        double distanceKm = 0.0;
        double grandTotal = total;
        
        // Calculate delivery fee if location params provided
        String restaurantIdParam = request.getParameter("restaurantId");
        String userLatParam = request.getParameter("userLat");
        String userLngParam = request.getParameter("userLng");
        
        if (restaurantIdParam != null && userLatParam != null && userLngParam != null && !cart.isEmpty()) {
            try {
                int restaurantId = Integer.parseInt(restaurantIdParam);
                double userLat = Double.parseDouble(userLatParam);
                double userLng = Double.parseDouble(userLngParam);
                
                Restaurant restaurant = restaurantDAO.getRestaurantById(restaurantId);
                if (restaurant != null) {
                    double[] distanceAndFee = DistanceCalculator.calculateDistanceAndFee(
                        userLat, userLng,
                        restaurant.getLatitude(), restaurant.getLongitude()
                    );
                    distanceKm = Math.round(distanceAndFee[0] * 100.0) / 100.0;
                    deliveryFee = distanceAndFee[1];
                    grandTotal = total + deliveryFee;
                }
            } catch (Exception e) {
                Logger.logError("Error calculating delivery fee", e);
            }
        }
        
        Map<String, Object> responseData = new HashMap<>();
        responseData.put("success", true);
        responseData.put("cart", cart);
        responseData.put("total", total);
        responseData.put("deliveryFee", deliveryFee);
        responseData.put("distanceKm", distanceKm);
        responseData.put("grandTotal", grandTotal);
        
        response.getWriter().write(gson.toJson(responseData));
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        String pathInfo = request.getPathInfo();
        Logger.logAPICall("POST", "/api/cart" + (pathInfo != null ? pathInfo : ""));
        
        try {
            if ("/add".equals(pathInfo) || "/items".equals(pathInfo)) {
                handleAddToCart(request, response);
            } else if ("/remove".equals(pathInfo)) {
                handleRemoveFromCart(request, response);
            } else if ("/clear".equals(pathInfo) || pathInfo == null || "/".equals(pathInfo)) {
                if (pathInfo == null || "/".equals(pathInfo)) {
                    handleClearCart(request, response);
                } else {
                    handleClearCart(request, response);
                }
            } else {
                sendError(response, 404, "Endpoint not found");
            }
        } catch (Exception e) {
            Logger.logError("Error in CartServlet", e);
            sendError(response, 500, "Internal server error: " + e.getMessage());
        }
    }
    
    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        String pathInfo = request.getPathInfo();
        Logger.logAPICall("PUT", "/api/cart" + (pathInfo != null ? pathInfo : ""));
        
        try {
            if (pathInfo != null && pathInfo.matches("/items/\\d+")) {
                handleUpdateCartItem(request, response, pathInfo);
            } else {
                sendError(response, 404, "Endpoint not found");
            }
        } catch (Exception e) {
            Logger.logError("Error in CartServlet", e);
            sendError(response, 500, "Internal server error: " + e.getMessage());
        }
    }
    
    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        String pathInfo = request.getPathInfo();
        Logger.logAPICall("DELETE", "/api/cart" + (pathInfo != null ? pathInfo : ""));
        
        try {
            if (pathInfo != null && pathInfo.matches("/items/\\d+")) {
                handleDeleteCartItem(request, response, pathInfo);
            } else if (pathInfo == null || "/".equals(pathInfo)) {
                handleClearCart(request, response);
            } else {
                sendError(response, 404, "Endpoint not found");
            }
        } catch (Exception e) {
            Logger.logError("Error in CartServlet", e);
            sendError(response, 500, "Internal server error: " + e.getMessage());
        }
    }
    
    private void handleAddToCart(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        try {
            CartItem newItem = gson.fromJson(request.getReader(), CartItem.class);
            
            if (newItem.getMenuItemId() <= 0 || newItem.getQuantity() <= 0) {
                sendError(response, 400, "Invalid item data");
                return;
            }
            
            HttpSession session = request.getSession();
            List<CartItem> cart = (List<CartItem>) session.getAttribute("cart");
            
            if (cart == null) {
                cart = new ArrayList<>();
            }
            
            // Check if item already exists in cart
            boolean found = false;
            for (CartItem item : cart) {
                if (item.getMenuItemId() == newItem.getMenuItemId()) {
                    item.setQuantity(item.getQuantity() + newItem.getQuantity());
                    found = true;
                    break;
                }
            }
            
            if (!found) {
                cart.add(newItem);
            }
            
            session.setAttribute("cart", cart);
            
            Integer userId = (Integer) session.getAttribute("userId");
            if (userId != null) {
                Logger.logUserAction("ADD_TO_CART: " + newItem.getName(), 
                                    (String) session.getAttribute("userEmail"));
            }
            
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("success", true);
            responseData.put("message", "Item added to cart");
            responseData.put("cart", cart);
            
            response.getWriter().write(gson.toJson(responseData));
            
        } catch (Exception e) {
            Logger.logError("Add to cart error", e);
            sendError(response, 500, "Failed to add item to cart");
        }
    }
    
    private void handleRemoveFromCart(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        try {
            Map<String, Object> data = gson.fromJson(request.getReader(), Map.class);
            int menuItemId = ((Double) data.get("menuItemId")).intValue();
            
            HttpSession session = request.getSession();
            List<CartItem> cart = (List<CartItem>) session.getAttribute("cart");
            
            if (cart != null) {
                cart.removeIf(item -> item.getMenuItemId() == menuItemId);
                session.setAttribute("cart", cart);
            }
            
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("success", true);
            responseData.put("message", "Item removed from cart");
            responseData.put("cart", cart);
            
            response.getWriter().write(gson.toJson(responseData));
            
        } catch (Exception e) {
            Logger.logError("Remove from cart error", e);
            sendError(response, 500, "Failed to remove item from cart");
        }
    }
    
    private void handleClearCart(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        HttpSession session = request.getSession();
        session.removeAttribute("cart");
        
        Map<String, Object> responseData = new HashMap<>();
        responseData.put("success", true);
        responseData.put("message", "Cart cleared");
        
        response.getWriter().write(gson.toJson(responseData));
    }
    
    private void handleUpdateCartItem(HttpServletRequest request, HttpServletResponse response, String pathInfo) 
            throws IOException {
        try {
            String[] parts = pathInfo.split("/");
            int menuItemId = Integer.parseInt(parts[2]);
            
            Map<String, Object> data = gson.fromJson(request.getReader(), Map.class);
            int newQuantity = ((Double) data.get("quantity")).intValue();
            
            if (newQuantity <= 0) {
                sendError(response, 400, "Quantity must be greater than 0");
                return;
            }
            
            HttpSession session = request.getSession();
            List<CartItem> cart = (List<CartItem>) session.getAttribute("cart");
            
            if (cart != null) {
                for (CartItem item : cart) {
                    if (item.getMenuItemId() == menuItemId) {
                        item.setQuantity(newQuantity);
                        break;
                    }
                }
                session.setAttribute("cart", cart);
            }
            
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("success", true);
            responseData.put("message", "Cart item updated");
            responseData.put("cart", cart);
            
            response.getWriter().write(gson.toJson(responseData));
            
        } catch (Exception e) {
            Logger.logError("Update cart item error", e);
            sendError(response, 500, "Failed to update cart item");
        }
    }
    
    private void handleDeleteCartItem(HttpServletRequest request, HttpServletResponse response, String pathInfo) 
            throws IOException {
        try {
            String[] parts = pathInfo.split("/");
            int menuItemId = Integer.parseInt(parts[2]);
            
            HttpSession session = request.getSession();
            List<CartItem> cart = (List<CartItem>) session.getAttribute("cart");
            
            if (cart != null) {
                cart.removeIf(item -> item.getMenuItemId() == menuItemId);
                session.setAttribute("cart", cart);
            }
            
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("success", true);
            responseData.put("message", "Item removed from cart");
            responseData.put("cart", cart);
            
            response.getWriter().write(gson.toJson(responseData));
            
        } catch (Exception e) {
            Logger.logError("Delete cart item error", e);
            sendError(response, 500, "Failed to remove item from cart");
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
