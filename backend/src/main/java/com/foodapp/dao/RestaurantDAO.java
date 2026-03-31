package com.foodapp.dao;

import com.foodapp.model.Restaurant;
import com.foodapp.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RestaurantDAO {
    
    public List<Restaurant> getAllRestaurants() throws SQLException {
        List<Restaurant> restaurants = new ArrayList<>();
        String sql = "SELECT * FROM restaurants ORDER BY rating DESC";
        
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Restaurant restaurant = new Restaurant();
                restaurant.setId(rs.getInt("id"));
                restaurant.setName(rs.getString("name"));
                restaurant.setDescription(rs.getString("description"));
                restaurant.setAddress(rs.getString("address"));
                restaurant.setImageUrl(rs.getString("image_url"));
                restaurant.setRating(rs.getDouble("rating"));
                restaurant.setLatitude(rs.getDouble("latitude"));
                restaurant.setLongitude(rs.getDouble("longitude"));
                
                restaurants.add(restaurant);
            }
        }
        
        return restaurants;
    }
    
    public Restaurant getRestaurantById(int id) throws SQLException {
        String sql = "SELECT * FROM restaurants WHERE id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Restaurant restaurant = new Restaurant();
                    restaurant.setId(rs.getInt("id"));
                    restaurant.setName(rs.getString("name"));
                    restaurant.setDescription(rs.getString("description"));
                    restaurant.setAddress(rs.getString("address"));
                    restaurant.setImageUrl(rs.getString("image_url"));
                    restaurant.setRating(rs.getDouble("rating"));
                    restaurant.setLatitude(rs.getDouble("latitude"));
                    restaurant.setLongitude(rs.getDouble("longitude"));
                    
                    return restaurant;
                }
            }
        }
        
        return null;
    }
}
