package com.urbankart.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PriceHistory {
    private int id;
    private int productId;
    private BigDecimal price;
    private LocalDateTime changeDate;

    public PriceHistory() {}

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getProductId() { return productId; }
    public void setProductId(int productId) { this.productId = productId; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public LocalDateTime getChangeDate() { return changeDate; }
    public void setChangeDate(LocalDateTime changeDate) { this.changeDate = changeDate; }
}