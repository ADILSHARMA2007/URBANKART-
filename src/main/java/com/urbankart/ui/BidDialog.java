package com.urbankart.ui;

import com.urbankart.dao.BidDAO;
import com.urbankart.dao.BidDAOImpl;
import com.urbankart.model.Bid;
import com.urbankart.model.Product;
import com.urbankart.model.User;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;

public class BidDialog extends JDialog {
    private boolean bidPlaced = false;
    private BigDecimal bidAmount;
    private BidDAO bidDAO;
    private Product product;
    private User currentUser;

    private JLabel currentBidLabel;
    private JTextField bidField;

    public BidDialog(Frame parent, Product product, User currentUser) {
        super(parent, "Place Bid", true);
        this.product = product;
        this.currentUser = currentUser;
        this.bidDAO = new BidDAOImpl();
        initializeUI();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());
        setSize(400, 200);
        setLocationRelativeTo(getParent());

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Product info
        JLabel productLabel = new JLabel("Product: " + product.getName(), SwingConstants.CENTER);
        productLabel.setFont(new Font("Arial", Font.BOLD, 14));

        // Current bid info
        Bid highestBid = bidDAO.getHighestBid(product.getId());
        BigDecimal currentBid = highestBid != null ? highestBid.getBidAmount() : product.getStartingPrice();
        currentBidLabel = new JLabel("Current Bid: $" + currentBid, SwingConstants.CENTER);

        // Bid input
        JPanel inputPanel = new JPanel(new FlowLayout());
        inputPanel.add(new JLabel("Your Bid: $"));
        bidField = new JTextField(10);
        inputPanel.add(bidField);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton placeBidButton = new JButton("Place Bid");
        JButton cancelButton = new JButton("Cancel");

        placeBidButton.addActionListener(e -> placeBid());
        cancelButton.addActionListener(e -> dispose());

        buttonPanel.add(cancelButton);
        buttonPanel.add(placeBidButton);

        mainPanel.add(productLabel, BorderLayout.NORTH);
        mainPanel.add(currentBidLabel, BorderLayout.CENTER);
        mainPanel.add(inputPanel, BorderLayout.SOUTH);

        add(mainPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void placeBid() {
        try {
            bidAmount = new BigDecimal(bidField.getText().trim());
            Bid highestBid = bidDAO.getHighestBid(product.getId());
            BigDecimal currentBid = highestBid != null ? highestBid.getBidAmount() : product.getStartingPrice();

            if (bidAmount.compareTo(currentBid) <= 0) {
                JOptionPane.showMessageDialog(this, "Your bid must be higher than the current bid.", "Invalid Bid", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Create and place the bid
            Bid bid = new Bid();
            bid.setProductId(product.getId());
            bid.setUserId(currentUser.getId());
            bid.setBidAmount(bidAmount);

            if (bidDAO.placeBid(bid)) {
                JOptionPane.showMessageDialog(this, "Bid placed successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                bidPlaced = true;
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to place bid. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter a valid bid amount.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isBidPlaced() {
        return bidPlaced;
    }

    public BigDecimal getBidAmount() {
        return bidAmount;
    }
}