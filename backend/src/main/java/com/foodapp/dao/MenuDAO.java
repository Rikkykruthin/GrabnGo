package com.foodapp.dao;

import com.foodapp.model.MenuItem;
import com.foodapp.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MenuDAO {
    
    public List<MenuItem> getMenuByRestaurantId(int restaurantId) throws SQLException {
        List<MenuItem> menuItems = new ArrayList<>();
        String sql = "SELECT * FROM menu_items WHERE restaurant_id = ? AND is_available = TRUE ORDER BY category, name";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, restaurantId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    MenuItem item = new MenuItem();
                    item.setId(rs.getInt("id"));
                    item.setRestaurantId(rs.getInt("restaurant_id"));
                    item.setName(rs.getString("name"));
                    item.setDescription(rs.getString("description"));
                    item.setPrice(rs.getDouble("price"));
                    item.setCategory(rs.getString("category"));
                    item.setImageUrl(rs.getString("image_url"));
                    item.setAvailable(rs.getBoolean("is_available"));
                    
                    menuItems.add(item);
                }
            }
        }
        
        return menuItems;
    }
    
    public MenuItem getMenuItemById(int id) throws SQLException {
        String sql = "SELECT * FROM menu_items WHERE id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    MenuItem item = new MenuItem();
                    item.setId(rs.getInt("id"));
                    item.setRestaurantId(rs.getInt("restaurant_id"));
                    item.setName(rs.getString("name"));
                    item.setDescription(rs.getString("description"));
                    item.setPrice(rs.getDouble("price"));
                    item.setCategory(rs.getString("category"));
                    item.setImageUrl(rs.getString("image_url"));
                    item.setAvailable(rs.getBoolean("is_available"));
                    
                    return item;
                }
            }
        }
        
        return null;
    }
}
