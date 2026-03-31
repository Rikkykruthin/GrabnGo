package com.foodapp.model;

public class CartItem {
    private int menuItemId;
    private String name;
    private double price;
    private int quantity;
    private int restaurantId;
    
    public CartItem() {}
    
    public CartItem(int menuItemId, String name, double price, int quantity, int restaurantId) {
        this.menuItemId = menuItemId;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.restaurantId = restaurantId;
    }
    
    // Getters and Setters
    public int getMenuItemId() { return menuItemId; }
    public void setMenuItemId(int menuItemId) { this.menuItemId = menuItemId; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
    
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    
    public int getRestaurantId() { return restaurantId; }
    public void setRestaurantId(int restaurantId) { this.restaurantId = restaurantId; }
    
    public double getSubtotal() {
        return price * quantity;
    }
}
