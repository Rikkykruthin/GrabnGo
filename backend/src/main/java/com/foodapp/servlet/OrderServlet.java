package com.foodapp.servlet;

import com.foodapp.dao.OrderDAO;
import com.foodapp.model.CartItem;
import com.foodapp.model.Order;
import com.foodapp.model.OrderItem;
import com.foodapp.util.EmailUtil;
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

public class OrderServlet extends HttpServlet {
    private OrderDAO orderDAO = new OrderDAO();
    private Gson gson = new Gson();
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        Logger.logAPICall("GET", "/api/orders");
        
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            sendError(response, 401, "Please login to view orders");
            return;
        }
        
        try {
            int userId = (Integer) session.getAttribute("userId");
            List<Order> orders = orderDAO.getOrdersByUserId(userId);
            
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("success", true);
            responseData.put("orders", orders);
            
            response.getWriter().write(gson.toJson(responseData));
            
        } catch (Exception e) {
            Logger.logError("Error fetching orders", e);
            sendError(response, 500, "Failed to fetch orders: " + e.getMessage());
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        String pathInfo = request.getPathInfo();
        Logger.logAPICall("POST", "/api/orders" + (pathInfo != null ? pathInfo : ""));
        
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            sendError(response, 401, "Please login to place order");
            return;
        }
        
        try {
            if (pathInfo == null || "/".equals(pathInfo)) {
                handlePlaceOrder(request, response, session);
            } else {
                sendError(response, 404, "Endpoint not found");
            }
        } catch (Exception e) {
            Logger.logError("Error in OrderServlet", e);
            sendError(response, 500, "Internal server error: " + e.getMessage());
        }
    }
    
    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        handlePatchOrPut(request, response);
    }
    
    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String method = request.getMethod();
        if ("PATCH".equalsIgnoreCase(method)) {
            handlePatchOrPut(request, response);
        } else {
            super.service(request, response);
        }
    }
    
    private void handlePatchOrPut(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        String pathInfo = request.getPathInfo();
        Logger.logAPICall("PUT", "/api/orders" + (pathInfo != null ? pathInfo : ""));
        
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            sendError(response, 401, "Please login to cancel order");
            return;
        }
        
        try {
            if (pathInfo != null && pathInfo.endsWith("/cancel")) {
                handleCancelOrder(request, response, session, pathInfo);
            } else {
                sendError(response, 404, "Endpoint not found");
            }
        } catch (Exception e) {
            Logger.logError("Error in OrderServlet", e);
            sendError(response, 500, "Internal server error: " + e.getMessage());
        }
    }
    
    private void handlePlaceOrder(HttpServletRequest request, HttpServletResponse response, HttpSession session) 
            throws IOException {
        try {
            int userId = (Integer) session.getAttribute("userId");
            String userName = (String) session.getAttribute("userName");
            String userEmail = (String) session.getAttribute("userEmail");
            
            List<CartItem> cart = (List<CartItem>) session.getAttribute("cart");
            
            if (cart == null || cart.isEmpty()) {
                sendError(response, 400, "Cart is empty");
                return;
            }
            
            Map<String, Object> orderData = gson.fromJson(request.getReader(), Map.class);
            String deliveryAddress = (String) orderData.get("deliveryAddress");
            double userLat = orderData.get("userLat") != null ? ((Number) orderData.get("userLat")).doubleValue() : 0.0;
            double userLng = orderData.get("userLng") != null ? ((Number) orderData.get("userLng")).doubleValue() : 0.0;
            
            if (deliveryAddress == null || deliveryAddress.trim().isEmpty()) {
                sendError(response, 400, "Delivery address is required");
                return;
            }
            
            if (userLat == 0.0 || userLng == 0.0) {
                sendError(response, 400, "User location is required");
                return;
            }
            
            // Get restaurant ID from first cart item
            int restaurantId = cart.get(0).getRestaurantId();
            
            // Get restaurant details for distance calculation
            com.foodapp.dao.RestaurantDAO restaurantDAO = new com.foodapp.dao.RestaurantDAO();
            com.foodapp.model.Restaurant restaurant = restaurantDAO.getRestaurantById(restaurantId);
            
            if (restaurant == null) {
                sendError(response, 404, "Restaurant not found");
                return;
            }
            
            // Calculate distance and delivery fee
            double[] distanceAndFee = com.foodapp.util.DistanceCalculator.calculateDistanceAndFee(
                userLat, userLng,
                restaurant.getLatitude(), restaurant.getLongitude()
            );
            
            double distanceKm = Math.round(distanceAndFee[0] * 100.0) / 100.0;
            double deliveryFee = distanceAndFee[1];
            
            // Calculate total
            double totalAmount = cart.stream().mapToDouble(CartItem::getSubtotal).sum();
            double grandTotal = totalAmount + deliveryFee;
            
            // Create order
            Order order = new Order();
            order.setUserId(userId);
            order.setRestaurantId(restaurantId);
            order.setTotalAmount(totalAmount);
            order.setDeliveryFee(deliveryFee);
            order.setDistanceKm(distanceKm);
            order.setGrandTotal(grandTotal);
            order.setStatus("PENDING");
            order.setDeliveryAddress(deliveryAddress);
            order.setUserLatitude(userLat);
            order.setUserLongitude(userLng);
            
            // Convert cart items to order items
            List<OrderItem> orderItems = new ArrayList<>();
            for (CartItem cartItem : cart) {
                OrderItem orderItem = new OrderItem(
                    cartItem.getMenuItemId(),
                    cartItem.getName(),
                    cartItem.getQuantity(),
                    cartItem.getPrice()
                );
                orderItems.add(orderItem);
            }
            order.setItems(orderItems);
            
            // Save order
            Order createdOrder = orderDAO.createOrder(order);
            
            // Send confirmation email
            EmailUtil.sendOrderConfirmationEmail(userEmail, userName, createdOrder.getId(), grandTotal);
            
            // Clear cart
            session.removeAttribute("cart");
            
            Logger.logUserAction("ORDER_PLACED: Order ID " + createdOrder.getId() + 
                               ", Distance: " + distanceKm + "km, Delivery Fee: ₹" + deliveryFee, userEmail);
            
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("success", true);
            responseData.put("message", "Order placed successfully");
            responseData.put("order", createdOrder);
            
            response.setStatus(201);
            response.getWriter().write(gson.toJson(responseData));
            
        } catch (Exception e) {
            Logger.logError("Place order error", e);
            sendError(response, 500, "Failed to place order: " + e.getMessage());
        }
    }
    
    private void handleCancelOrder(HttpServletRequest request, HttpServletResponse response, 
                                   HttpSession session, String pathInfo) throws IOException {
        try {
            int userId = (Integer) session.getAttribute("userId");
            String userName = (String) session.getAttribute("userName");
            String userEmail = (String) session.getAttribute("userEmail");
            
            // Extract order ID from path
            String[] parts = pathInfo.split("/");
            int orderId = Integer.parseInt(parts[1]);
            
            boolean cancelled = orderDAO.cancelOrder(orderId, userId);
            
            if (cancelled) {
                EmailUtil.sendOrderCancellationEmail(userEmail, userName, orderId);
                Logger.logUserAction("ORDER_CANCELLED: Order ID " + orderId, userEmail);
                
                Map<String, Object> responseData = new HashMap<>();
                responseData.put("success", true);
                responseData.put("message", "Order cancelled successfully");
                
                response.getWriter().write(gson.toJson(responseData));
            } else {
                sendError(response, 400, "Cannot cancel order. Order may not exist or is already being processed");
            }
            
        } catch (NumberFormatException e) {
            sendError(response, 400, "Invalid order ID");
        } catch (Exception e) {
            Logger.logError("Cancel order error", e);
            sendError(response, 500, "Failed to cancel order: " + e.getMessage());
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
