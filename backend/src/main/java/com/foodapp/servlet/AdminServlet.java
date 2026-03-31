package com.foodapp.servlet;

import com.foodapp.dao.OrderDAO;
import com.foodapp.dao.UserDAO;
import com.foodapp.model.Order;
import com.foodapp.model.User;
import com.foodapp.util.Logger;
import com.google.gson.Gson;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdminServlet extends HttpServlet {
    private UserDAO userDAO = new UserDAO();
    private OrderDAO orderDAO = new OrderDAO();
    private Gson gson = new Gson();
    
    private boolean isAdmin(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) return false;
        
        String role = (String) session.getAttribute("userRole");
        return "ADMIN".equals(role);
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        if (!isAdmin(request)) {
            sendError(response, 403, "Access denied. Admin only.");
            return;
        }
        
        String pathInfo = request.getPathInfo();
        Logger.logAPICall("GET", "/api/admin" + (pathInfo != null ? pathInfo : ""));
        
        try {
            if ("/users".equals(pathInfo)) {
                handleGetAllUsers(request, response);
            } else if ("/orders".equals(pathInfo)) {
                handleGetAllOrders(request, response);
            } else if ("/dashboard".equals(pathInfo)) {
                handleGetDashboard(request, response);
            } else {
                sendError(response, 404, "Endpoint not found");
            }
        } catch (Exception e) {
            Logger.logError("Error in AdminServlet", e);
            sendError(response, 500, "Internal server error: " + e.getMessage());
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        if (!isAdmin(request)) {
            sendError(response, 403, "Access denied. Admin only.");
            return;
        }
        
        String pathInfo = request.getPathInfo();
        Logger.logAPICall("POST", "/api/admin" + (pathInfo != null ? pathInfo : ""));
        
        try {
            if (pathInfo != null && pathInfo.matches("/orders/\\d+/status")) {
                handleUpdateOrderStatus(request, response, pathInfo);
            } else {
                sendError(response, 404, "Endpoint not found");
            }
        } catch (Exception e) {
            Logger.logError("Error in AdminServlet", e);
            sendError(response, 500, "Internal server error: " + e.getMessage());
        }
    }
    
    private void handleGetAllUsers(HttpServletRequest request, HttpServletResponse response) 
            throws Exception {
        List<User> users = userDAO.getAllUsers();
        
        Map<String, Object> responseData = new HashMap<>();
        responseData.put("success", true);
        responseData.put("users", users);
        responseData.put("total", users.size());
        
        response.getWriter().write(gson.toJson(responseData));
    }
    
    private void handleGetAllOrders(HttpServletRequest request, HttpServletResponse response) 
            throws Exception {
        List<Order> orders = orderDAO.getAllOrders();
        
        Map<String, Object> responseData = new HashMap<>();
        responseData.put("success", true);
        responseData.put("orders", orders);
        responseData.put("total", orders.size());
        
        response.getWriter().write(gson.toJson(responseData));
    }
    
    private void handleGetDashboard(HttpServletRequest request, HttpServletResponse response) 
            throws Exception {
        List<User> users = userDAO.getAllUsers();
        List<Order> orders = orderDAO.getAllOrders();
        
        // Calculate statistics
        long totalUsers = users.stream().filter(u -> "CUSTOMER".equals(u.getRole())).count();
        long totalOrders = orders.size();
        long pendingOrders = orders.stream().filter(o -> "PENDING".equals(o.getStatus())).count();
        long completedOrders = orders.stream().filter(o -> "DELIVERED".equals(o.getStatus())).count();
        double totalRevenue = orders.stream()
            .filter(o -> !"CANCELLED".equals(o.getStatus()))
            .mapToDouble(Order::getGrandTotal)
            .sum();
        
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalUsers", totalUsers);
        stats.put("totalOrders", totalOrders);
        stats.put("pendingOrders", pendingOrders);
        stats.put("completedOrders", completedOrders);
        stats.put("totalRevenue", totalRevenue);
        
        Map<String, Object> responseData = new HashMap<>();
        responseData.put("success", true);
        responseData.put("stats", stats);
        responseData.put("recentOrders", orders.subList(0, Math.min(10, orders.size())));
        
        response.getWriter().write(gson.toJson(responseData));
    }
    
    private void handleUpdateOrderStatus(HttpServletRequest request, HttpServletResponse response, String pathInfo) 
            throws Exception {
        String[] parts = pathInfo.split("/");
        int orderId = Integer.parseInt(parts[2]);
        
        Map<String, String> data = gson.fromJson(request.getReader(), Map.class);
        String newStatus = data.get("status");
        
        if (newStatus == null || newStatus.trim().isEmpty()) {
            sendError(response, 400, "Status is required");
            return;
        }
        
        boolean updated = orderDAO.updateOrderStatus(orderId, newStatus);
        
        if (updated) {
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("success", true);
            responseData.put("message", "Order status updated successfully");
            response.getWriter().write(gson.toJson(responseData));
        } else {
            sendError(response, 404, "Order not found");
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
