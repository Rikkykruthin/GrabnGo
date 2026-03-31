package com.foodapp.dao;

import com.foodapp.model.Order;
import com.foodapp.model.OrderItem;
import com.foodapp.util.DBConnection;
import com.foodapp.util.Logger;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrderDAO {
    
    public Order createOrder(Order order) throws SQLException {
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);
            
            // Insert order
            String orderSql = "INSERT INTO orders (user_id, restaurant_id, total_amount, delivery_fee, distance_km, grand_total, status, delivery_address, user_latitude, user_longitude) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement orderStmt = conn.prepareStatement(orderSql, Statement.RETURN_GENERATED_KEYS);
            
            orderStmt.setInt(1, order.getUserId());
            orderStmt.setInt(2, order.getRestaurantId());
            orderStmt.setDouble(3, order.getTotalAmount());
            orderStmt.setDouble(4, order.getDeliveryFee());
            orderStmt.setDouble(5, order.getDistanceKm());
            orderStmt.setDouble(6, order.getGrandTotal());
            orderStmt.setString(7, order.getStatus());
            orderStmt.setString(8, order.getDeliveryAddress());
            orderStmt.setDouble(9, order.getUserLatitude());
            orderStmt.setDouble(10, order.getUserLongitude());
            
            orderStmt.executeUpdate();
            
            ResultSet rs = orderStmt.getGeneratedKeys();
            if (rs.next()) {
                order.setId(rs.getInt(1));
            }
            
            // Insert order items
            String itemSql = "INSERT INTO order_items (order_id, menu_item_id, menu_item_name, quantity, price) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement itemStmt = conn.prepareStatement(itemSql);
            
            for (OrderItem item : order.getItems()) {
                itemStmt.setInt(1, order.getId());
                itemStmt.setInt(2, item.getMenuItemId());
                itemStmt.setString(3, item.getMenuItemName());
                itemStmt.setInt(4, item.getQuantity());
                itemStmt.setDouble(5, item.getPrice());
                itemStmt.addBatch();
            }
            
            itemStmt.executeBatch();
            conn.commit();
            
            Logger.log("Order created: Order ID " + order.getId() + " for User ID " + order.getUserId());
            return order;
            
        } catch (SQLException e) {
            if (conn != null) {
                conn.rollback();
            }
            throw e;
        } finally {
            if (conn != null) {
                conn.setAutoCommit(true);
                conn.close();
            }
        }
    }
    
    public List<Order> getOrdersByUserId(int userId) throws SQLException {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT o.*, r.name as restaurant_name FROM orders o " +
                    "JOIN restaurants r ON o.restaurant_id = r.id " +
                    "WHERE o.user_id = ? ORDER BY o.created_at DESC";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Order order = new Order();
                    order.setId(rs.getInt("id"));
                    order.setUserId(rs.getInt("user_id"));
                    order.setRestaurantId(rs.getInt("restaurant_id"));
                    order.setRestaurantName(rs.getString("restaurant_name"));
                    order.setTotalAmount(rs.getDouble("total_amount"));
                    order.setDeliveryFee(rs.getDouble("delivery_fee"));
                    order.setDistanceKm(rs.getDouble("distance_km"));
                    order.setGrandTotal(rs.getDouble("grand_total"));
                    order.setStatus(rs.getString("status"));
                    order.setDeliveryAddress(rs.getString("delivery_address"));
                    order.setUserLatitude(rs.getDouble("user_latitude"));
                    order.setUserLongitude(rs.getDouble("user_longitude"));
                    order.setCreatedAt(rs.getTimestamp("created_at"));
                    
                    // Get order items
                    order.setItems(getOrderItems(order.getId()));
                    
                    orders.add(order);
                }
            }
        }
        
        return orders;
    }
    
    private List<OrderItem> getOrderItems(int orderId) throws SQLException {
        List<OrderItem> items = new ArrayList<>();
        String sql = "SELECT * FROM order_items WHERE order_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, orderId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    OrderItem item = new OrderItem();
                    item.setId(rs.getInt("id"));
                    item.setOrderId(rs.getInt("order_id"));
                    item.setMenuItemId(rs.getInt("menu_item_id"));
                    item.setMenuItemName(rs.getString("menu_item_name"));
                    item.setQuantity(rs.getInt("quantity"));
                    item.setPrice(rs.getDouble("price"));
                    
                    items.add(item);
                }
            }
        }
        
        return items;
    }
    
    public boolean cancelOrder(int orderId, int userId) throws SQLException {
        String sql = "UPDATE orders SET status = 'CANCELLED' WHERE id = ? AND user_id = ? AND status = 'PENDING'";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, orderId);
            stmt.setInt(2, userId);
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                Logger.log("Order cancelled: Order ID " + orderId);
                return true;
            }
        }
        
        return false;
    }
    
    public Order getOrderById(int orderId, int userId) throws SQLException {
        String sql = "SELECT o.*, r.name as restaurant_name FROM orders o " +
                    "JOIN restaurants r ON o.restaurant_id = r.id " +
                    "WHERE o.id = ? AND o.user_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, orderId);
            stmt.setInt(2, userId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Order order = new Order();
                    order.setId(rs.getInt("id"));
                    order.setUserId(rs.getInt("user_id"));
                    order.setRestaurantId(rs.getInt("restaurant_id"));
                    order.setRestaurantName(rs.getString("restaurant_name"));
                    order.setTotalAmount(rs.getDouble("total_amount"));
                    order.setDeliveryFee(rs.getDouble("delivery_fee"));
                    order.setDistanceKm(rs.getDouble("distance_km"));
                    order.setGrandTotal(rs.getDouble("grand_total"));
                    order.setStatus(rs.getString("status"));
                    order.setDeliveryAddress(rs.getString("delivery_address"));
                    order.setUserLatitude(rs.getDouble("user_latitude"));
                    order.setUserLongitude(rs.getDouble("user_longitude"));
                    order.setCreatedAt(rs.getTimestamp("created_at"));
                    
                    order.setItems(getOrderItems(order.getId()));
                    
                    return order;
                }
            }
        }
        
        return null;
    }
}
