package com.urbankart.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PriceAlert {
    private int id;
    private int userId;
    private int productId;
    private BigDecimal targetPrice;
    private boolean isActive;
    private LocalDateTime createdAt;
    private Product product;

    public PriceAlert() {}

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public int getProductId() { return productId; }
    public void setProductId(int productId) { this.productId = productId; }

    public BigDecimal getTargetPrice() { return targetPrice; }
    public void setTargetPrice(BigDecimal targetPrice) { this.targetPrice = targetPrice; }

    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }
}