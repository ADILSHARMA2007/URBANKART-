package com.urbankart.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class BidInfo {
    private Bid bid;
    private String productName;
    private String bidderUsername;

    public BidInfo(Bid bid, String productName, String bidderUsername) {
        this.bid = bid;
        this.productName = productName;
        this.bidderUsername = bidderUsername;
    }

    // Getters and Setters
    public Bid getBid() { return bid; }
    public void setBid(Bid bid) { this.bid = bid; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public String getBidderUsername() { return bidderUsername; }
    public void setBidderUsername(String bidderUsername) { this.bidderUsername = bidderUsername; }
}