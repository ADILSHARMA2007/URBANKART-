package com.urbankart.ui;

import com.urbankart.dao.*;
import com.urbankart.model.*;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.util.List;

public class CartPanel extends JPanel {
    private User currentUser;
    private CartDAO cartDAO;
    private ProductDAO productDAO;
    private OrderDAO orderDAO;
    private OrderItemDAO orderItemDAO;
    private JPanel cartItemsPanel;
    private JLabel totalLabel;
    private List<CartItem> currentCartItems;

    public CartPanel(User user) {
        this.currentUser = user;
        this.cartDAO = new CartDAOImpl();
        this.productDAO = new ProductDAOImpl();
        this.orderDAO = new OrderDAOImpl();
        this.orderItemDAO = new OrderItemDAOImpl();

        System.out.println("=== CART PANEL CONSTRUCTOR ===");
        System.out.println("User: " + user.getUsername() + " (ID: " + user.getId() + ")");
        System.out.println("Role: " + user.getRole());

        initializeUI();
        loadCartItems();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());

        // Header
        JLabel headerLabel = new JLabel("My Shopping Cart", SwingConstants.CENTER);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 18));
        add(headerLabel, BorderLayout.NORTH);

        // Cart items panel
        cartItemsPanel = new JPanel();
        cartItemsPanel.setLayout(new BoxLayout(cartItemsPanel, BoxLayout.Y_AXIS));
        JScrollPane scrollPane = new JScrollPane(cartItemsPanel);

        // Refresh button
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton refreshButton = new JButton("Refresh Cart");
        refreshButton.addActionListener(e -> {
            System.out.println("🔄 Manual refresh triggered");
            loadCartItems();
        });
        topPanel.add(refreshButton);

        // Bottom panel with total and checkout button
        JPanel bottomPanel = new JPanel(new BorderLayout());
        totalLabel = new JLabel("Total: $0.00", SwingConstants.RIGHT);
        totalLabel.setFont(new Font("Arial", Font.BOLD, 16));

        JButton checkoutButton = new JButton("Proceed to Checkout");
        checkoutButton.setBackground(new Color(0, 102, 0));
        checkoutButton.setForeground(Color.BLACK);
        checkoutButton.addActionListener(e -> checkout());

        JButton clearButton = new JButton("Clear Cart");
        clearButton.setBackground(new Color(204, 0, 0));
        clearButton.setForeground(Color.BLACK);
        clearButton.addActionListener(e -> clearCart());

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(clearButton);
        buttonPanel.add(checkoutButton);

        bottomPanel.add(totalLabel, BorderLayout.CENTER);
        bottomPanel.add(buttonPanel, BorderLayout.EAST);

        // Add all panels
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.add(topPanel, BorderLayout.NORTH);
        contentPanel.add(scrollPane, BorderLayout.CENTER);
        contentPanel.add(bottomPanel, BorderLayout.SOUTH);

        add(contentPanel, BorderLayout.CENTER);
    }

    public void refreshCart() {
        System.out.println("🔄 CartPanel.refreshCart() called");
        loadCartItems();
    }

    private void loadCartItems() {
        System.out.println("=== LOADING CART ITEMS ===");
        System.out.println("User ID: " + currentUser.getId());

        currentCartItems = cartDAO.getCartItemsByUser(currentUser.getId());
        displayCartItems(currentCartItems);
        updateTotal(currentCartItems);

        System.out.println("=== CART LOADING COMPLETE ===");
    }

    private void displayCartItems(List<CartItem> cartItems) {
        cartItemsPanel.removeAll();

        System.out.println("Displaying " + cartItems.size() + " cart items");

        if (cartItems.isEmpty()) {
            JLabel emptyLabel = new JLabel("Your cart is empty.", SwingConstants.CENTER);
            emptyLabel.setFont(new Font("Arial", Font.ITALIC, 16));
            cartItemsPanel.add(emptyLabel);
            System.out.println("🛒 Cart is empty - showing empty message");
        } else {
            for (CartItem item : cartItems) {
                // Get product details for display
                Product product = productDAO.getProductById(item.getProductId());
                if (product != null) {
                    cartItemsPanel.add(createCartItemCard(item, product));
                    cartItemsPanel.add(Box.createRigidArea(new Dimension(0, 10)));
                    System.out.println("📦 Displaying cart item: " + product.getName() + " (Qty: " + item.getQuantity() + ")");
                } else {
                    System.err.println("❌ Product not found for cart item: " + item.getProductId());
                }
            }
            System.out.println("🛒 Displayed " + cartItems.size() + " items in cart UI");
        }

        cartItemsPanel.revalidate();
        cartItemsPanel.repaint();
    }

    private JPanel createCartItemCard(CartItem item, Product product) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        card.setMaximumSize(new Dimension(800, 100));

        // Product info
        JPanel infoPanel = new JPanel(new GridLayout(3, 1));
        JLabel nameLabel = new JLabel(product.getName());
        nameLabel.setFont(new Font("Arial", Font.BOLD, 14));

        String priceInfo = String.format("Price: $%.2f", product.getPrice());
        JLabel priceLabel = new JLabel(priceInfo);
        priceLabel.setFont(new Font("Arial", Font.PLAIN, 12));

        String quantityInfo = String.format("Quantity: %d | Total: $%.2f",
                item.getQuantity(),
                product.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
        JLabel quantityLabel = new JLabel(quantityInfo);
        quantityLabel.setFont(new Font("Arial", Font.PLAIN, 12));

        infoPanel.add(nameLabel);
        infoPanel.add(priceLabel);
        infoPanel.add(quantityLabel);

        // Action buttons
        JPanel actionPanel = new JPanel(new FlowLayout());

        JButton removeButton = new JButton("Remove");
        removeButton.addActionListener(e -> {
            System.out.println("🗑️ Removing cart item ID: " + item.getId());
            removeItem(item.getId());
        });

        JSpinner quantitySpinner = new JSpinner(new SpinnerNumberModel(item.getQuantity(), 1, 100, 1));
        JButton updateButton = new JButton("Update");
        updateButton.addActionListener(e -> {
            int newQuantity = (Integer) quantitySpinner.getValue();
            System.out.println("✏️ Updating cart item ID: " + item.getId() + " to quantity: " + newQuantity);
            updateQuantity(item.getId(), newQuantity);
        });

        actionPanel.add(new JLabel("Qty:"));
        actionPanel.add(quantitySpinner);
        actionPanel.add(updateButton);
        actionPanel.add(removeButton);

        card.add(infoPanel, BorderLayout.CENTER);
        card.add(actionPanel, BorderLayout.EAST);

        return card;
    }

    private void updateTotal(List<CartItem> cartItems) {
        BigDecimal total = BigDecimal.ZERO;
        int itemCount = 0;

        for (CartItem item : cartItems) {
            Product product = productDAO.getProductById(item.getProductId());
            if (product != null && product.getPrice() != null) {
                BigDecimal itemTotal = product.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
                total = total.add(itemTotal);
                itemCount++;
            }
        }

        totalLabel.setText(String.format("Total (%d items): $%.2f", itemCount, total));
        System.out.println("💰 Updated total: $" + total + " for " + itemCount + " items");
    }

    private void removeItem(int cartItemId) {
        System.out.println("=== REMOVING ITEM FROM CART ===");
        if (cartDAO.removeFromCart(cartItemId)) {
            System.out.println("✅ Successfully removed item from cart");
            JOptionPane.showMessageDialog(this, "Item removed from cart.");
            loadCartItems(); // Refresh the display
        } else {
            System.out.println("❌ Failed to remove item from cart");
            JOptionPane.showMessageDialog(this, "Failed to remove item.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateQuantity(int cartItemId, int quantity) {
        System.out.println("=== UPDATING CART ITEM QUANTITY ===");
        if (cartDAO.updateCartItemQuantity(cartItemId, quantity)) {
            System.out.println("✅ Successfully updated quantity");
            loadCartItems(); // Refresh the display
        } else {
            System.out.println("❌ Failed to update quantity");
            JOptionPane.showMessageDialog(this, "Failed to update quantity.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void checkout() {
        if (currentCartItems.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Your cart is empty.", "Checkout", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Calculate total
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (CartItem item : currentCartItems) {
            Product product = productDAO.getProductById(item.getProductId());
            if (product != null && product.getPrice() != null) {
                BigDecimal itemTotal = product.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
                totalAmount = totalAmount.add(itemTotal);
            }
        }

        // Show checkout dialog
        CheckoutDialog checkoutDialog = new CheckoutDialog(
                (java.awt.Frame) SwingUtilities.getWindowAncestor(this),
                currentUser,
                currentCartItems,
                totalAmount
        );

        checkoutDialog.setVisible(true);

        if (checkoutDialog.isOrderConfirmed()) {
            // Process the order
            processOrder(checkoutDialog.getShippingAddress(), checkoutDialog.getPaymentMethod(), totalAmount);
        }
    }
    private void processOrder(String shippingAddress, String paymentMethod, BigDecimal totalAmount) {
        try {
            // Create order
            Order order = new Order();
            order.setUserId(currentUser.getId());
            order.setTotalAmount(totalAmount);
            order.setShippingAddress(shippingAddress);
            order.setPaymentMethod(paymentMethod);
            order.setStatus(Order.OrderStatus.PENDING);

            if (orderDAO.createOrder(order)) {
                System.out.println("✅ Order created with ID: " + order.getId());

                // Create order items
                boolean allItemsCreated = true;
                for (CartItem cartItem : currentCartItems) {
                    Product product = productDAO.getProductById(cartItem.getProductId());
                    if (product != null) {
                        OrderItem orderItem = new OrderItem();
                        orderItem.setOrderId(order.getId());
                        orderItem.setProductId(cartItem.getProductId());
                        orderItem.setQuantity(cartItem.getQuantity());
                        orderItem.setUnitPrice(product.getPrice());
                        orderItem.setTotalPrice(product.getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity())));

                        if (!orderItemDAO.createOrderItem(orderItem)) {
                            allItemsCreated = false;
                            break;
                        }
                    }
                }

                if (allItemsCreated) {
                    // Clear cart and show success
                    cartDAO.clearCart(currentUser.getId());

                    JOptionPane.showMessageDialog(this,
                            "Order placed successfully!\n\n" +
                                    "Order ID: " + order.getId() + "\n" +
                                    "Total: $" + totalAmount + "\n" +
                                    "Shipping to: " + shippingAddress + "\n" +
                                    "Payment: " + paymentMethod,
                            "Order Confirmed",
                            JOptionPane.INFORMATION_MESSAGE);

                    loadCartItems(); // Refresh cart (should be empty now)
                } else {
                    JOptionPane.showMessageDialog(this,
                            "There was an error creating order items. Please try again.",
                            "Order Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this,
                        "Failed to create order. Please try again.",
                        "Order Error",
                        JOptionPane.ERROR_MESSAGE);
            }

        } catch (Exception e) {
            System.err.println("❌ Error during checkout: " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "An error occurred during checkout: " + e.getMessage(),
                    "Checkout Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearCart() {
        int result = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to clear your cart?",
                "Clear Cart",
                JOptionPane.YES_NO_OPTION);

        if (result == JOptionPane.YES_OPTION) {
            if (cartDAO.clearCart(currentUser.getId())) {
                System.out.println("✅ Cart cleared successfully");
                JOptionPane.showMessageDialog(this, "Cart cleared.");
                loadCartItems();
            } else {
                System.out.println("❌ Failed to clear cart");
                JOptionPane.showMessageDialog(this, "Failed to clear cart.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}