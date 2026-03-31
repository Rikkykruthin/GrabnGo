package com.foodapp.model;

public class Restaurant {
    private int id;
    private String name;
    private String description;
    private String address;
    private String imageUrl;
    private double rating;
    private double latitude;
    private double longitude;
    
    public Restaurant() {}
    
    public Restaurant(int id, String name, String description, String address, String imageUrl, double rating, double latitude, double longitude) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.address = address;
        this.imageUrl = imageUrl;
        this.rating = rating;
        this.latitude = latitude;
        this.longitude = longitude;
    }
    
    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    
    public double getRating() { return rating; }
    public void setRating(double rating) { this.rating = rating; }
    
    public double getLatitude() { return latitude; }
    public void setLatitude(double latitude) { this.latitude = latitude; }
    
    public double getLongitude() { return longitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }
}
