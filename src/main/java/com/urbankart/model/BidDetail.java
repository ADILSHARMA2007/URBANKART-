package com.urbankart.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class BidDetail {
    private int id;
    private int productId;
    private int userId;
    private BigDecimal bidAmount;
    private LocalDateTime bidTime;
    private boolean isWinning;
    private String productName;
    private String bidderUsername;

    public BidDetail() {}

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getProductId() { return productId; }
    public void setProductId(int productId) { this.productId = productId; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public BigDecimal getBidAmount() { return bidAmount; }
    public void setBidAmount(BigDecimal bidAmount) { this.bidAmount = bidAmount; }

    public LocalDateTime getBidTime() { return bidTime; }
    public void setBidTime(LocalDateTime bidTime) { this.bidTime = bidTime; }

    public boolean isWinning() { return isWinning; }
    public void setWinning(boolean winning) { isWinning = winning; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public String getBidderUsername() { return bidderUsername; }
    public void setBidderUsername(String bidderUsername) { this.bidderUsername = bidderUsername; }
}