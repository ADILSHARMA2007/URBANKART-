package com.urbankart.ui;

import com.urbankart.dao.CartDAO;
import com.urbankart.dao.CartDAOImpl;
import com.urbankart.dao.ProductDAO;
import com.urbankart.dao.ProductDAOImpl;
import com.urbankart.model.Product;
import com.urbankart.model.User;
import com.urbankart.model.CartItem; // ADD THIS IMPORT

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class ProductBrowserPanel extends JPanel {
    private User currentUser;
    private ProductDAO productDAO;
    private CartDAO cartDAO;
    private JPanel productsPanel;
    private JTextField searchField;
    private JComboBox<String> categoryComboBox;
    private JComboBox<String> typeComboBox;

    public ProductBrowserPanel(User user) {
        this.currentUser = user;
        this.productDAO = new ProductDAOImpl();
        this.cartDAO = new CartDAOImpl();
        initializeUI();
        loadProducts();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());

        // Search and filter panel
        JPanel filterPanel = new JPanel(new FlowLayout());

        searchField = new JTextField(20);
        searchField.setToolTipText("Search products...");

        categoryComboBox = new JComboBox<>(new String[]{"All Categories", "Electronics", "Fashion", "Home", "Sports", "Furniture"});
        typeComboBox = new JComboBox<>(new String[]{"All Types", "Buy", "Bid", "Lend"});

        JButton searchButton = new JButton("Search");
        JButton clearButton = new JButton("Clear");

        searchButton.addActionListener(e -> searchProducts());
        clearButton.addActionListener(e -> clearFilters());

        filterPanel.add(new JLabel("Search:"));
        filterPanel.add(searchField);
        filterPanel.add(new JLabel("Category:"));
        filterPanel.add(categoryComboBox);
        filterPanel.add(new JLabel("Type:"));
        filterPanel.add(typeComboBox);
        filterPanel.add(searchButton);
        filterPanel.add(clearButton);

        // Products panel
        productsPanel = new JPanel();
        productsPanel.setLayout(new BoxLayout(productsPanel, BoxLayout.Y_AXIS));
        JScrollPane scrollPane = new JScrollPane(productsPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        add(filterPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void loadProducts() {
        List<Product> products = productDAO.getAllProducts();
        displayProducts(products);
    }

    private void searchProducts() {
        String query = searchField.getText().trim();
        String category = (String) categoryComboBox.getSelectedItem();
        String type = (String) typeComboBox.getSelectedItem();

        List<Product> products;

        if (!query.isEmpty()) {
            products = productDAO.searchProducts(query);
        } else {
            products = productDAO.getAllProducts();
        }

        // Apply category filter
        if (!"All Categories".equals(category)) {
            products.removeIf(product -> !category.equals(product.getCategory()));
        }

        // Apply type filter
        if (!"All Types".equals(type)) {
            Product.ListingType listingType = Product.ListingType.valueOf(type.toUpperCase());
            products.removeIf(product -> product.getListType() != listingType);
        }

        displayProducts(products);
    }

    private void clearFilters() {
        searchField.setText("");
        categoryComboBox.setSelectedIndex(0);
        typeComboBox.setSelectedIndex(0);
        loadProducts();
    }

    private void displayProducts(List<Product> products) {
        productsPanel.removeAll();

        if (products.isEmpty()) {
            JLabel noProductsLabel = new JLabel("No products found.", SwingConstants.CENTER);
            noProductsLabel.setFont(new Font("Arial", Font.ITALIC, 16));
            productsPanel.add(noProductsLabel);
        } else {
            for (Product product : products) {
                productsPanel.add(createProductCard(product));
                productsPanel.add(Box.createRigidArea(new Dimension(0, 10))); // Spacing
            }
        }

        productsPanel.revalidate();
        productsPanel.repaint();
    }

    private JPanel createProductCard(Product product) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        card.setMaximumSize(new Dimension(800, 120));

        // Left side: Product image and basic info
        JPanel leftPanel = new JPanel(new BorderLayout());

        // Product image (placeholder)
        JLabel imageLabel = new JLabel("📷", SwingConstants.CENTER);
        imageLabel.setPreferredSize(new Dimension(100, 100));
        imageLabel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

        // Product info
        JPanel infoPanel = new JPanel(new GridLayout(4, 1));
        infoPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));

        JLabel nameLabel = new JLabel(product.getName());
        nameLabel.setFont(new Font("Arial", Font.BOLD, 14));

        JLabel descLabel = new JLabel(product.getDescription());
        descLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        descLabel.setForeground(Color.GRAY);

        JLabel categoryLabel = new JLabel("Category: " + product.getCategory());
        categoryLabel.setFont(new Font("Arial", Font.PLAIN, 12));

        JLabel sellerLabel = new JLabel("Seller ID: " + product.getSellerId());
        sellerLabel.setFont(new Font("Arial", Font.PLAIN, 12));

        infoPanel.add(nameLabel);
        infoPanel.add(descLabel);
        infoPanel.add(categoryLabel);
        infoPanel.add(sellerLabel);

        leftPanel.add(imageLabel, BorderLayout.WEST);
        leftPanel.add(infoPanel, BorderLayout.CENTER);

        // Right side: Price and action buttons
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setPreferredSize(new Dimension(200, 100));

        JPanel pricePanel = new JPanel(new GridLayout(3, 1));

        String priceText = "";
        switch (product.getListType()) {
            case BUY:
                priceText = String.format("Price: $%.2f", product.getPrice());
                break;
            case BID:
                priceText = String.format("Current Bid: $%.2f", product.getCurrentBid());
                break;
            case LEND:
                priceText = String.format("Rent: $%.2f/day", product.getPrice());
                break;
        }

        JLabel priceLabel = new JLabel(priceText);
        priceLabel.setFont(new Font("Arial", Font.BOLD, 14));
        priceLabel.setForeground(new Color(0, 102, 0));

        JLabel typeLabel = new JLabel("Type: " + product.getListType());
        typeLabel.setFont(new Font("Arial", Font.PLAIN, 12));

        JLabel statusLabel = new JLabel("Status: " + product.getAvailabilityStatus());
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 12));

        pricePanel.add(priceLabel);
        pricePanel.add(typeLabel);
        pricePanel.add(statusLabel);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton actionButton = new JButton(getActionButtonText(product.getListType()));
        actionButton.addActionListener(e -> handleProductAction(product, actionButton.getText()));

        JButton viewButton = new JButton("View Details");
        viewButton.addActionListener(e -> openProductDetail(product));

        buttonPanel.add(actionButton);
        buttonPanel.add(viewButton);

        rightPanel.add(pricePanel, BorderLayout.CENTER);
        rightPanel.add(buttonPanel, BorderLayout.SOUTH);

        card.add(leftPanel, BorderLayout.CENTER);
        card.add(rightPanel, BorderLayout.EAST);

        return card;
    }

    private String getActionButtonText(Product.ListingType type) {
        switch (type) {
            case BUY: return "Add to Cart";
            case BID: return "Place Bid";
            case LEND: return "Rent Now";
            default: return "View";
        }
    }

    private void handleProductAction(Product product, String actionText) {
        switch (product.getListType()) {
            case BUY:
                addToCart(product);
                break;
            case BID:
                placeBid(product);
                break;
            case LEND:
                rentProduct(product);
                break;
        }
    }

    private void addToCart(Product product) {
        if (product.getListType() != Product.ListingType.BUY) {
            JOptionPane.showMessageDialog(this,
                    "This product is not available for direct purchase.",
                    "Invalid Action",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (product.getAvailabilityStatus() != Product.AvailabilityStatus.AVAILABLE) {
            JOptionPane.showMessageDialog(this,
                    "This product is currently unavailable.",
                    "Out of Stock",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Ask for quantity
        String quantityStr = JOptionPane.showInputDialog(this,
                "Enter quantity for '" + product.getName() + "':",
                "1");

        if (quantityStr == null) return; // User cancelled

        try {
            int quantity = Integer.parseInt(quantityStr);
            if (quantity <= 0) {
                JOptionPane.showMessageDialog(this,
                        "Please enter a valid quantity.",
                        "Invalid Quantity",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            System.out.println("=== USER ACTION: Adding to Cart ===");
            System.out.println("User: " + currentUser.getUsername() + " (ID: " + currentUser.getId() + ")");
            System.out.println("Product: " + product.getName() + " (ID: " + product.getId() + ")");
            System.out.println("Quantity: " + quantity);

            // Add to cart
            boolean success = cartDAO.addToCart(currentUser.getId(), product.getId(), quantity);

            if (success) {
                System.out.println("✅ SUCCESS: Cart item added to database");
                JOptionPane.showMessageDialog(this,
                        "Successfully added '" + product.getName() + "' to your cart!",
                        "Added to Cart",
                        JOptionPane.INFORMATION_MESSAGE);

                // Verify the item was actually added
                CartItem verifyItem = cartDAO.getCartItem(currentUser.getId(), product.getId());
                if (verifyItem != null) {
                    System.out.println("✅ VERIFICATION: Cart item confirmed in database - ID: " + verifyItem.getId());
                } else {
                    System.out.println("❌ VERIFICATION FAILED: Cart item not found after add");
                }
            } else {
                System.out.println("❌ FAILED: Cart item not added to database");
                JOptionPane.showMessageDialog(this,
                        "Failed to add item to cart. Please try again.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                    "Please enter a valid number for quantity.",
                    "Invalid Input",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void placeBid(Product product) {
        if (product.getListType() != Product.ListingType.BID) {
            JOptionPane.showMessageDialog(this,
                    "This product is not available for bidding.",
                    "Invalid Action",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Check if auction has ended
        if (product.getAuctionEndTime() != null &&
                product.getAuctionEndTime().isBefore(java.time.LocalDateTime.now())) {
            JOptionPane.showMessageDialog(this,
                    "This auction has ended.",
                    "Auction Ended",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            // Get the parent frame properly
            Window parentWindow = SwingUtilities.getWindowAncestor(this);
            java.awt.Frame parentFrame = null;
            if (parentWindow instanceof java.awt.Frame) {
                parentFrame = (java.awt.Frame) parentWindow;
            }

            BidDialog bidDialog = new BidDialog(parentFrame, product, currentUser);
            bidDialog.setVisible(true);

            if (bidDialog.isBidPlaced()) {
                // Refresh the product info to show the new bid
                loadProducts();
            }
        } catch (Exception e) {
            System.err.println("Error creating bid dialog: " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Error opening bid dialog: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void rentProduct(Product product) {
        if (product.getListType() != Product.ListingType.LEND) {
            JOptionPane.showMessageDialog(this,
                    "This product is not available for rental.",
                    "Invalid Action",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (product.getAvailabilityStatus() != Product.AvailabilityStatus.AVAILABLE) {
            JOptionPane.showMessageDialog(this,
                    "This product is currently unavailable for rental.",
                    "Unavailable",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            // Get the parent frame properly
            Window parentWindow = SwingUtilities.getWindowAncestor(this);
            java.awt.Frame parentFrame = null;
            if (parentWindow instanceof java.awt.Frame) {
                parentFrame = (java.awt.Frame) parentWindow;
            }

            RentalDialog rentalDialog = new RentalDialog(parentFrame, product, currentUser);
            rentalDialog.setVisible(true);

            if (rentalDialog.isRentalConfirmed()) {
                // Refresh the product info
                loadProducts();
            }
        } catch (Exception e) {
            System.err.println("Error creating rental dialog: " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Error opening rental dialog: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void openProductDetail(Product product) {
        String message = String.format(
                "Product Details:\n\nName: %s\nDescription: %s\nCategory: %s\nType: %s\nStatus: %s\n\nPrice: %s",
                product.getName(),
                product.getDescription(),
                product.getCategory(),
                product.getListType(),
                product.getAvailabilityStatus(),
                getPriceDisplay(product)
        );
        JOptionPane.showMessageDialog(this, message, "Product Details", JOptionPane.INFORMATION_MESSAGE);
    }

    private String getPriceDisplay(Product product) {
        switch (product.getListType()) {
            case BUY:
                return String.format("$%.2f", product.getPrice());
            case BID:
                return String.format("Starting: $%.2f, Current: $%.2f",
                        product.getStartingPrice(), product.getCurrentBid());
            case LEND:
                return String.format("$%.2f per day", product.getPrice());
            default:
                return "N/A";
        }
    }
}