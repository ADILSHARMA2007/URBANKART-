package com.urbankart.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class Loan {
    private int id;
    private int productId;
    private int userId;
    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal totalRentalFee;
    private LoanStatus status;
    private LocalDateTime createdAt;
    private Product product;
    private User user;

    public enum LoanStatus {
        PENDING, ACTIVE, COMPLETED, CANCELLED
    }

    public Loan() {}

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getProductId() { return productId; }
    public void setProductId(int productId) { this.productId = productId; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    public BigDecimal getTotalRentalFee() { return totalRentalFee; }
    public void setTotalRentalFee(BigDecimal totalRentalFee) { this.totalRentalFee = totalRentalFee; }

    public LoanStatus getStatus() { return status; }
    public void setStatus(LoanStatus status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
}