package com.foodapp.model;

import java.sql.Timestamp;
import java.util.List;

public class Order {
    private int id;
    private int userId;
    private int restaurantId;
    private String restaurantName;
    private double totalAmount;
    private double deliveryFee;
    private double distanceKm;
    private double grandTotal;
    private String status;
    private String deliveryAddress;
    private double userLatitude;
    private double userLongitude;
    private Timestamp createdAt;
    private List<OrderItem> items;
    
    public Order() {}
    
    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    
    public int getRestaurantId() { return restaurantId; }
    public void setRestaurantId(int restaurantId) { this.restaurantId = restaurantId; }
    
    public String getRestaurantName() { return restaurantName; }
    public void setRestaurantName(String restaurantName) { this.restaurantName = restaurantName; }
    
    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }
    
    public double getDeliveryFee() { return deliveryFee; }
    public void setDeliveryFee(double deliveryFee) { this.deliveryFee = deliveryFee; }
    
    public double getDistanceKm() { return distanceKm; }
    public void setDistanceKm(double distanceKm) { this.distanceKm = distanceKm; }
    
    public double getGrandTotal() { return grandTotal; }
    public void setGrandTotal(double grandTotal) { this.grandTotal = grandTotal; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public String getDeliveryAddress() { return deliveryAddress; }
    public void setDeliveryAddress(String deliveryAddress) { this.deliveryAddress = deliveryAddress; }
    
    public double getUserLatitude() { return userLatitude; }
    public void setUserLatitude(double userLatitude) { this.userLatitude = userLatitude; }
    
    public double getUserLongitude() { return userLongitude; }
    public void setUserLongitude(double userLongitude) { this.userLongitude = userLongitude; }
    
    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
    
    public List<OrderItem> getItems() { return items; }
    public void setItems(List<OrderItem> items) { this.items = items; }
}
