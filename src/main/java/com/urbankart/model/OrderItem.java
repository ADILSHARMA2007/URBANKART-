package com.urbankart.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class OrderItem {
    private int id;
    private int orderId;
    private int productId;
    private int quantity;
    private BigDecimal unitPrice;
    private BigDecimal totalPrice;
    private Product product;

    public OrderItem() {}

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getOrderId() { return orderId; }
    public void setOrderId(int orderId) { this.orderId = orderId; }

    public int getProductId() { return productId; }
    public void setProductId(int productId) { this.productId = productId; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public BigDecimal getUnitPrice() { return unitPrice; }
    public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }

    public BigDecimal getTotalPrice() { return totalPrice; }
    public void setTotalPrice(BigDecimal totalPrice) { this.totalPrice = totalPrice; }

    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }
}