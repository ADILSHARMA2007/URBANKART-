package com.urbankart.ui;

import com.urbankart.dao.ProductDAO;
import com.urbankart.dao.ProductDAOImpl;
import com.urbankart.model.CartItem;
import com.urbankart.model.User;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.util.List;

public class CheckoutDialog extends JDialog {
    private boolean orderConfirmed = false;
    private String shippingAddress;
    private String paymentMethod;

    private JTextField addressField;
    private JComboBox<String> paymentComboBox;
    private JTextArea orderSummaryArea;
    private ProductDAO productDAO;

    public CheckoutDialog(Frame parent, User user, List<CartItem> cartItems, BigDecimal totalAmount) {
        super(parent, "Checkout", true);
        this.productDAO = new ProductDAOImpl();
        initializeUI(user, cartItems, totalAmount);
    }

    private void initializeUI(User user, List<CartItem> cartItems, BigDecimal totalAmount) {
        setLayout(new BorderLayout());
        setSize(500, 600);
        setLocationRelativeTo(getParent());
        setResizable(false);

        // Header
        JLabel headerLabel = new JLabel("Checkout", SwingConstants.CENTER);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 18));
        headerLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(headerLabel, BorderLayout.NORTH);

        // Main content panel
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Order summary
        JPanel summaryPanel = createSummaryPanel(cartItems, totalAmount);

        // Shipping information
        JPanel shippingPanel = createShippingPanel(user);

        // Payment information
        JPanel paymentPanel = createPaymentPanel();

        // Buttons
        JPanel buttonPanel = createButtonPanel();

        // Add all panels to main panel
        mainPanel.add(summaryPanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        mainPanel.add(shippingPanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        mainPanel.add(paymentPanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        mainPanel.add(buttonPanel);

        add(mainPanel, BorderLayout.CENTER);
    }

    private JPanel createSummaryPanel(List<CartItem> cartItems, BigDecimal totalAmount) {
        JPanel summaryPanel = new JPanel(new BorderLayout());
        summaryPanel.setBorder(BorderFactory.createTitledBorder("Order Summary"));

        orderSummaryArea = new JTextArea(10, 40);
        orderSummaryArea.setEditable(false);
        orderSummaryArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        updateOrderSummary(cartItems, totalAmount);

        JScrollPane summaryScroll = new JScrollPane(orderSummaryArea);
        summaryPanel.add(summaryScroll, BorderLayout.CENTER);

        return summaryPanel;
    }

    private JPanel createShippingPanel(User user) {
        JPanel shippingPanel = new JPanel(new BorderLayout());
        shippingPanel.setBorder(BorderFactory.createTitledBorder("Shipping Information"));

        JPanel shippingForm = new JPanel(new GridLayout(3, 2, 5, 5));
        shippingForm.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        shippingForm.add(new JLabel("Full Name:"));
        JTextField nameField = new JTextField(user.getUsername());
        shippingForm.add(nameField);

        shippingForm.add(new JLabel("Email:"));
        JTextField emailField = new JTextField(user.getEmail());
        emailField.setEditable(false);
        shippingForm.add(emailField);

        shippingForm.add(new JLabel("Shipping Address:"));
        addressField = new JTextField();
        addressField.setText("123 Main St, City, State 12345");
        shippingForm.add(addressField);

        shippingPanel.add(shippingForm, BorderLayout.CENTER);
        return shippingPanel;
    }

    private JPanel createPaymentPanel() {
        JPanel paymentPanel = new JPanel(new BorderLayout());
        paymentPanel.setBorder(BorderFactory.createTitledBorder("Payment Information"));

        JPanel paymentForm = new JPanel(new GridLayout(3, 2, 5, 5));
        paymentForm.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        paymentForm.add(new JLabel("Payment Method:"));
        paymentComboBox = new JComboBox<>(new String[]{
                "Credit Card", "Debit Card", "PayPal", "Cash on Delivery"
        });
        paymentForm.add(paymentComboBox);

        paymentForm.add(new JLabel("Card Number:"));
        JTextField cardField = new JTextField("**** **** **** 1234");
        paymentForm.add(cardField);

        paymentForm.add(new JLabel("Expiry Date:"));
        JTextField expiryField = new JTextField("MM/YY");
        paymentForm.add(expiryField);

        paymentPanel.add(paymentForm, BorderLayout.CENTER);
        return paymentPanel;
    }

    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton confirmButton = new JButton("Confirm Order");
        JButton cancelButton = new JButton("Cancel");

        confirmButton.setBackground(new Color(0, 102, 0));
        confirmButton.setForeground(Color.BLACK);
        confirmButton.addActionListener(e -> confirmOrder());

        cancelButton.setBackground(new Color(204, 0, 0));
        cancelButton.setForeground(Color.BLACK);
        cancelButton.addActionListener(e -> cancelOrder());

        buttonPanel.add(cancelButton);
        buttonPanel.add(confirmButton);

        return buttonPanel;
    }

    private void updateOrderSummary(List<CartItem> cartItems, BigDecimal totalAmount) {
        StringBuilder summary = new StringBuilder();
        summary.append("ITEMS IN YOUR ORDER:\n");
        summary.append("====================\n\n");

        BigDecimal calculatedTotal = BigDecimal.ZERO;

        for (CartItem item : cartItems) {
            var product = productDAO.getProductById(item.getProductId());
            if (product != null) {
                BigDecimal itemPrice = product.getPrice() != null ? product.getPrice() : BigDecimal.ZERO;
                BigDecimal itemTotal = itemPrice.multiply(BigDecimal.valueOf(item.getQuantity()));
                calculatedTotal = calculatedTotal.add(itemTotal);

                summary.append(String.format("%s\n", product.getName()));
                summary.append(String.format("  Quantity: %d\n", item.getQuantity()));
                summary.append(String.format("  Price: $%.2f each\n", itemPrice));
                summary.append(String.format("  Total: $%.2f\n\n", itemTotal));
            } else {
                summary.append(String.format("Product ID: %d\n", item.getProductId()));
                summary.append(String.format("  Quantity: %d\n\n", item.getQuantity()));
            }
        }

        summary.append("====================\n");
        summary.append(String.format("ORDER TOTAL: $%.2f\n", calculatedTotal));
        summary.append("====================\n\n");
        summary.append("Thank you for shopping with UrbanKart!");

        orderSummaryArea.setText(summary.toString());
    }

    private void confirmOrder() {
        shippingAddress = addressField.getText().trim();
        paymentMethod = (String) paymentComboBox.getSelectedItem();

        if (shippingAddress.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please enter a shipping address.",
                    "Missing Information",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (shippingAddress.length() < 10) {
            JOptionPane.showMessageDialog(this,
                    "Please enter a complete shipping address.",
                    "Invalid Address",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirmation = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to place this order?\n\n" +
                        "Total amount will be charged to your " + paymentMethod + ".\n" +
                        "Items will be shipped to: " + shippingAddress,
                "Confirm Order",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (confirmation == JOptionPane.YES_OPTION) {
            orderConfirmed = true;
            dispose();
        }
    }

    private void cancelOrder() {
        int result = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to cancel this order?",
                "Cancel Order",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (result == JOptionPane.YES_OPTION) {
            orderConfirmed = false;
            dispose();
        }
    }

    public boolean isOrderConfirmed() {
        return orderConfirmed;
    }

    public String getShippingAddress() {
        return shippingAddress;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }
}