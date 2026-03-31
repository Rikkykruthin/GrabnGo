package com.foodapp.model;

public class User {
    private int id;
    private String email;
    private String password;
    private String name;
    private String phone;
    private String role;
    
    public User() {
        this.role = "CUSTOMER"; // Default role
    }
    
    public User(int id, String email, String name, String phone) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.phone = phone;
        this.role = "CUSTOMER";
    }
    
    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    
    public boolean isAdmin() {
        return "ADMIN".equals(role);
    }
}
