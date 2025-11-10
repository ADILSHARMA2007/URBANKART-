package com.urbankart.ui;

import com.urbankart.model.Product;
import com.urbankart.model.User;

import javax.swing.*;
import java.awt.*;

public class ProductDetailPanel extends JPanel {
    private Product product;
    private User currentUser;

    public ProductDetailPanel(Product product, User user) {
        this.product = product;
        this.currentUser = user;
        initializeUI();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Main content panel
        JPanel mainPanel = new JPanel(new BorderLayout(20, 20));

        // Left panel - Product image and basic info
        JPanel leftPanel = new JPanel(new BorderLayout());

        // Product image (placeholder)
        JLabel imageLabel = new JLabel("📷 Product Image", SwingConstants.CENTER);
        imageLabel.setPreferredSize(new Dimension(300, 300));
        imageLabel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        imageLabel.setFont(new Font("Arial", Font.PLAIN, 16));

        // Basic info
        JPanel infoPanel = new JPanel(new GridLayout(4, 1, 5, 5));
        infoPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        JLabel sellerLabel = new JLabel("Seller: User #" + product.getSellerId());
        JLabel categoryLabel = new JLabel("Category: " + product.getCategory());
        JLabel typeLabel = new JLabel("Listing Type: " + product.getListType());
        JLabel statusLabel = new JLabel("Status: " + product.getAvailabilityStatus());

        infoPanel.add(sellerLabel);
        infoPanel.add(categoryLabel);
        infoPanel.add(typeLabel);
        infoPanel.add(statusLabel);

        leftPanel.add(imageLabel, BorderLayout.CENTER);
        leftPanel.add(infoPanel, BorderLayout.SOUTH);

        // Right panel - Detailed info and actions
        JPanel rightPanel = new JPanel(new BorderLayout());

        // Product details
        JPanel detailsPanel = new JPanel();
        detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));
        detailsPanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));

        JLabel nameLabel = new JLabel(product.getName());
        nameLabel.setFont(new Font("Arial", Font.BOLD, 24));

        JTextArea descArea = new JTextArea(product.getDescription());
        descArea.setEditable(false);
        descArea.setLineWrap(true);
        descArea.setWrapStyleWord(true);
        descArea.setBackground(getBackground());
        descArea.setFont(new Font("Arial", Font.PLAIN, 14));
        JScrollPane descScroll = new JScrollPane(descArea);
        descScroll.setPreferredSize(new Dimension(400, 100));

        // Price information
        JPanel pricePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pricePanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        JLabel priceLabel = new JLabel();
        priceLabel.setFont(new Font("Arial", Font.BOLD, 20));
        priceLabel.setForeground(new Color(0, 102, 0));

        switch (product.getListType()) {
            case BUY:
                priceLabel.setText(String.format("Price: $%.2f", product.getPrice()));
                break;
            case BID:
                priceLabel.setText(String.format("Current Bid: $%.2f", product.getCurrentBid()));
                break;
            case LEND:
                priceLabel.setText(String.format("Rental Fee: $%.2f per day", product.getPrice()));
                break;
        }

        pricePanel.add(priceLabel);

        // Action buttons
        JPanel actionPanel = new JPanel(new FlowLayout());

        JButton primaryAction = new JButton(getPrimaryActionText());
        primaryAction.setBackground(new Color(0, 102, 204));
        primaryAction.setForeground(Color.BLACK);
        primaryAction.addActionListener(e -> performPrimaryAction());

        JButton secondaryAction = new JButton(getSecondaryActionText());
        secondaryAction.addActionListener(e -> performSecondaryAction());

        JButton backButton = new JButton("Back to Browse");
        backButton.addActionListener(e -> goBack());

        actionPanel.add(primaryAction);
        actionPanel.add(secondaryAction);
        actionPanel.add(backButton);

        detailsPanel.add(nameLabel);
        detailsPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        detailsPanel.add(descScroll);
        detailsPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        detailsPanel.add(pricePanel);
        detailsPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        detailsPanel.add(actionPanel);

        rightPanel.add(detailsPanel, BorderLayout.CENTER);

        mainPanel.add(leftPanel, BorderLayout.WEST);
        mainPanel.add(rightPanel, BorderLayout.CENTER);

        add(mainPanel, BorderLayout.CENTER);
    }

    private String getPrimaryActionText() {
        switch (product.getListType()) {
            case BUY: return "Add to Cart";
            case BID: return "Place Bid";
            case LEND: return "Rent Now";
            default: return "View";
        }
    }

    private String getSecondaryActionText() {
        switch (product.getListType()) {
            case BUY: return "Buy Now";
            case BID: return "View Bids";
            case LEND: return "View Availability";
            default: return "More Info";
        }
    }

    private void performPrimaryAction() {
        switch (product.getListType()) {
            case BUY:
                JOptionPane.showMessageDialog(this, "Added to cart!", "Success", JOptionPane.INFORMATION_MESSAGE);
                break;
            case BID:
                JOptionPane.showMessageDialog(this, "Bid dialog to be implemented.", "Info", JOptionPane.INFORMATION_MESSAGE);
                break;
            case LEND:
                JOptionPane.showMessageDialog(this, "Rental dialog to be implemented.", "Info", JOptionPane.INFORMATION_MESSAGE);
                break;
        }
    }

    private void performSecondaryAction() {
        switch (product.getListType()) {
            case BUY:
                JOptionPane.showMessageDialog(this, "Direct purchase to be implemented.", "Info", JOptionPane.INFORMATION_MESSAGE);
                break;
            case BID:
                JOptionPane.showMessageDialog(this, "Bid history to be implemented.", "Info", JOptionPane.INFORMATION_MESSAGE);
                break;
            case LEND:
                JOptionPane.showMessageDialog(this, "Availability calendar to be implemented.", "Info", JOptionPane.INFORMATION_MESSAGE);
                break;
        }
    }

    private void goBack() {
        // This would navigate back to the product browser
        JOptionPane.showMessageDialog(this, "Navigation back to browser to be implemented.", "Info", JOptionPane.INFORMATION_MESSAGE);
    }
}