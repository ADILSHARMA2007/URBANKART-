package com.urbankart.model;

public class User {
    private int id;
    private String username;
    private String password;
    private String email;
    private UserRole role;
    private boolean isActive;

    public enum UserRole {
        BUYER, SELLER, ADMIN
    }

    public User() {}

    public User(int id, String username, String password, String email, UserRole role, boolean isActive) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.email = email;
        this.role = role;
        this.isActive = isActive;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public UserRole getRole() { return role; }
    public void setRole(UserRole role) { this.role = role; }

    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }
}