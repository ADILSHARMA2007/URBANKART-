package com.urbankart.ui;

import com.urbankart.dao.LoanDAO;
import com.urbankart.dao.LoanDAOImpl;
import com.urbankart.model.Loan;
import com.urbankart.model.Product;
import com.urbankart.model.User;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class RentalDialog extends JDialog {
    private boolean rentalConfirmed = false;
    private LoanDAO loanDAO;
    private Product product;
    private User currentUser;

    private JSpinner startDateSpinner;
    private JSpinner endDateSpinner;
    private JLabel totalCostLabel;

    public RentalDialog(Frame parent, Product product, User currentUser) {
        super(parent, "Rent Product", true);
        this.product = product;
        this.currentUser = currentUser;
        this.loanDAO = new LoanDAOImpl();
        initializeUI();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());
        setSize(400, 300);
        setLocationRelativeTo(getParent());

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Product info
        JLabel productLabel = new JLabel("Product: " + product.getName(), SwingConstants.CENTER);
        productLabel.setFont(new Font("Arial", Font.BOLD, 14));

        // Daily rate
        JLabel rateLabel = new JLabel("Daily Rate: $" + product.getPrice(), SwingConstants.CENTER);

        // Date selection
        JPanel datePanel = new JPanel(new GridLayout(2, 2, 5, 5));
        datePanel.add(new JLabel("Start Date:"));
        startDateSpinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor startEditor = new JSpinner.DateEditor(startDateSpinner, "yyyy-MM-dd");
        startDateSpinner.setEditor(startEditor);
        datePanel.add(startDateSpinner);

        datePanel.add(new JLabel("End Date:"));
        endDateSpinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor endEditor = new JSpinner.DateEditor(endDateSpinner, "yyyy-MM-dd");
        endDateSpinner.setEditor(endEditor);
        datePanel.add(endDateSpinner);

        // Total cost
        totalCostLabel = new JLabel("Total Cost: $0.00", SwingConstants.CENTER);
        updateTotalCost();

        // Add listeners to update total cost when dates change
        startDateSpinner.addChangeListener(e -> updateTotalCost());
        endDateSpinner.addChangeListener(e -> updateTotalCost());

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton rentButton = new JButton("Rent Now");
        JButton cancelButton = new JButton("Cancel");

        rentButton.addActionListener(e -> rentProduct());
        cancelButton.addActionListener(e -> dispose());

        buttonPanel.add(cancelButton);
        buttonPanel.add(rentButton);

        mainPanel.add(productLabel, BorderLayout.NORTH);
        mainPanel.add(rateLabel, BorderLayout.CENTER);
        mainPanel.add(datePanel, BorderLayout.SOUTH);

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(rateLabel, BorderLayout.NORTH);
        centerPanel.add(datePanel, BorderLayout.CENTER);
        centerPanel.add(totalCostLabel, BorderLayout.SOUTH);

        mainPanel.add(centerPanel, BorderLayout.CENTER);

        add(mainPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void updateTotalCost() {
        LocalDate startDate = ((java.util.Date) startDateSpinner.getValue()).toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();
        LocalDate endDate = ((java.util.Date) endDateSpinner.getValue()).toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();

        if (startDate.isAfter(endDate)) {
            totalCostLabel.setText("Invalid dates");
            return;
        }

        long days = ChronoUnit.DAYS.between(startDate, endDate) + 1; // Inclusive
        BigDecimal total = product.getPrice().multiply(BigDecimal.valueOf(days));
        totalCostLabel.setText("Total Cost: $" + total);
    }

    private void rentProduct() {
        LocalDate startDate = ((java.util.Date) startDateSpinner.getValue()).toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();
        LocalDate endDate = ((java.util.Date) endDateSpinner.getValue()).toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();

        if (startDate.isAfter(endDate)) {
            JOptionPane.showMessageDialog(this, "End date must be after start date.", "Invalid Dates", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (startDate.isBefore(LocalDate.now())) {
            JOptionPane.showMessageDialog(this, "Start date cannot be in the past.", "Invalid Dates", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Check availability
        if (!loanDAO.isProductAvailable(product.getId(), startDate, endDate)) {
            JOptionPane.showMessageDialog(this, "Product is not available for the selected dates.", "Not Available", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Calculate total cost
        long days = ChronoUnit.DAYS.between(startDate, endDate) + 1;
        BigDecimal totalCost = product.getPrice().multiply(BigDecimal.valueOf(days));

        // Create loan
        Loan loan = new Loan();
        loan.setProductId(product.getId());
        loan.setUserId(currentUser.getId());
        loan.setStartDate(startDate);
        loan.setEndDate(endDate);
        loan.setTotalRentalFee(totalCost);
        loan.setStatus(Loan.LoanStatus.PENDING); // Or ACTIVE, depending on your business logic

        if (loanDAO.createLoan(loan)) {
            JOptionPane.showMessageDialog(this, "Product rented successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            rentalConfirmed = true;
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to rent product. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isRentalConfirmed() {
        return rentalConfirmed;
    }
}