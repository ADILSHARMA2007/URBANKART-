package com.urbankart.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Product {
    private int id;
    private int sellerId;
    private String name;
    private String description;
    private String category;
    private String imagePath;
    private ListingType listType;
    private BigDecimal price;
    private BigDecimal startingPrice;
    private BigDecimal currentBid;
    private Integer currentBidderId;
    private LocalDateTime auctionEndTime;
    private AvailabilityStatus availabilityStatus;
    private boolean isActive;

    public enum ListingType {
        BUY, BID, LEND
    }

    public enum AvailabilityStatus {
        AVAILABLE, OUT_OF_STOCK, ON_LOAN
    }

    public Product() {}

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getSellerId() { return sellerId; }
    public void setSellerId(int sellerId) { this.sellerId = sellerId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getImagePath() { return imagePath; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }

    public ListingType getListType() { return listType; }
    public void setListType(ListingType listType) { this.listType = listType; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public BigDecimal getStartingPrice() { return startingPrice; }
    public void setStartingPrice(BigDecimal startingPrice) { this.startingPrice = startingPrice; }

    public BigDecimal getCurrentBid() { return currentBid; }
    public void setCurrentBid(BigDecimal currentBid) { this.currentBid = currentBid; }

    public Integer getCurrentBidderId() { return currentBidderId; }
    public void setCurrentBidderId(Integer currentBidderId) { this.currentBidderId = currentBidderId; }

    public LocalDateTime getAuctionEndTime() { return auctionEndTime; }
    public void setAuctionEndTime(LocalDateTime auctionEndTime) { this.auctionEndTime = auctionEndTime; }

    public AvailabilityStatus getAvailabilityStatus() { return availabilityStatus; }
    public void setAvailabilityStatus(AvailabilityStatus availabilityStatus) { this.availabilityStatus = availabilityStatus; }

    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }
}