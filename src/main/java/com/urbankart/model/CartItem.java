package com.urbankart.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class CartItem {
    private int id;
    private int userId;
    private int productId;
    private int quantity;
    private LocalDateTime addedAt;
    private Product product; // Joined product data

    public CartItem() {}

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public int getProductId() { return productId; }
    public void setProductId(int productId) { this.productId = productId; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public LocalDateTime getAddedAt() { return addedAt; }
    public void setAddedAt(LocalDateTime addedAt) { this.addedAt = addedAt; }

    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }


}