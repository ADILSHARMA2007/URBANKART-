package com.urbankart.model;

import java.time.LocalDateTime;

public class Notification {
    private int id;
    private int userId;
    private String title;
    private String message;
    private boolean isRead;
    private LocalDateTime createdAt;
    private NotificationType type;

    public enum NotificationType {
        PRICE_ALERT, BID_WON, AUCTION_ENDED, SYSTEM
    }

    public Notification() {}

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public boolean isRead() { return isRead; }
    public void setRead(boolean read) { isRead = read; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public NotificationType getType() { return type; }
    public void setType(NotificationType type) { this.type = type; }
}