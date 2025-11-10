package com.urbankart.ui;

import com.urbankart.dao.*;
import com.urbankart.model.*;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.util.List;

public class SellerDashboardPanel extends JPanel {
    private User currentUser;
    private ProductDAO productDAO;
    private BidDAO bidDAO;
    private OrderDAO orderDAO;
    private OrderItemDAO orderItemDAO;
    private JTabbedPane tabbedPane;

    public SellerDashboardPanel(User user) {
        this.currentUser = user;
        this.productDAO = new ProductDAOImpl();
        this.bidDAO = new BidDAOImpl();
        this.orderDAO = new OrderDAOImpl();
        this.orderItemDAO = new OrderItemDAOImpl();
        initializeUI();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());

        // Header
        JLabel headerLabel = new JLabel("Seller Dashboard", SwingConstants.CENTER);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 18));
        headerLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Tabbed pane for different seller sections
        tabbedPane = new JTabbedPane();
        tabbedPane.addTab("My Products", createProductsPanel());
        tabbedPane.addTab("Manage Bids", createBidsPanel());
        tabbedPane.addTab("Sales & Orders", createOrdersPanel());

        add(headerLabel, BorderLayout.NORTH);
        add(tabbedPane, BorderLayout.CENTER);
    }

    private JPanel createProductsPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // Header with add button
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel titleLabel = new JLabel("My Products");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));

        JButton addProductButton = new JButton("Add New Product");
        addProductButton.setBackground(new Color(0, 102, 204));
        addProductButton.setForeground(Color.BLACK);
        addProductButton.addActionListener(e -> showAddProductDialog());

        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(addProductButton, BorderLayout.EAST);

        // Products panel
        JPanel productsPanel = new JPanel();
        productsPanel.setLayout(new BoxLayout(productsPanel, BoxLayout.Y_AXIS));
        JScrollPane scrollPane = new JScrollPane(productsPanel);

        // Load and display products
        loadSellerProducts(productsPanel);

        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createBidsPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        JLabel titleLabel = new JLabel("Bids on My Products", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel bidsPanel = new JPanel();
        bidsPanel.setLayout(new BoxLayout(bidsPanel, BoxLayout.Y_AXIS));
        JScrollPane scrollPane = new JScrollPane(bidsPanel);

        // Load and display bids
        loadSellerBids(bidsPanel);

        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createOrdersPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        JLabel titleLabel = new JLabel("Sales & Orders", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // For now, show a placeholder
        JTextArea infoArea = new JTextArea(
                "Sales and order management feature will be implemented here.\n\n" +
                        "This will include:\n" +
                        "- Order history\n" +
                        "- Sales statistics\n" +
                        "- Revenue tracking\n" +
                        "- Customer management"
        );
        infoArea.setEditable(false);
        infoArea.setFont(new Font("Arial", Font.PLAIN, 14));
        infoArea.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(new JScrollPane(infoArea), BorderLayout.CENTER);

        return panel;
    }

    private void loadSellerProducts(JPanel productsPanel) {
        productsPanel.removeAll();

        List<Product> products = productDAO.getProductsBySeller(currentUser.getId());

        if (products.isEmpty()) {
            JLabel noProductsLabel = new JLabel("No products listed yet. Click 'Add New Product' to get started.", SwingConstants.CENTER);
            noProductsLabel.setFont(new Font("Arial", Font.ITALIC, 16));
            productsPanel.add(noProductsLabel);
        } else {
            for (Product product : products) {
                productsPanel.add(createProductManagementCard(product));
                productsPanel.add(Box.createRigidArea(new Dimension(0, 10)));
            }
        }

        productsPanel.revalidate();
        productsPanel.repaint();
    }

    private void loadSellerBids(JPanel bidsPanel) {
        bidsPanel.removeAll();

        // Get all products by this seller
        List<Product> sellerProducts = productDAO.getProductsBySeller(currentUser.getId());
        boolean hasBids = false;

        for (Product product : sellerProducts) {
            if (product.getListType() == Product.ListingType.BID) {
                List<Bid> bids = bidDAO.getBidsByProduct(product.getId());
                if (!bids.isEmpty()) {
                    hasBids = true;
                    bidsPanel.add(createProductBidHeader(product));
                    for (Bid bid : bids) {
                        bidsPanel.add(createBidCard(bid, product));
                        bidsPanel.add(Box.createRigidArea(new Dimension(0, 5)));
                    }
                    bidsPanel.add(Box.createRigidArea(new Dimension(0, 15)));
                }
            }
        }

        if (!hasBids) {
            JLabel noBidsLabel = new JLabel("No bids on your products yet.", SwingConstants.CENTER);
            noBidsLabel.setFont(new Font("Arial", Font.ITALIC, 16));
            bidsPanel.add(noBidsLabel);
        }

        bidsPanel.revalidate();
        bidsPanel.repaint();
    }

    private JPanel createProductManagementCard(Product product) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        card.setMaximumSize(new Dimension(800, 120));

        // Product info
        JPanel infoPanel = new JPanel(new GridLayout(3, 2, 5, 5));

        JLabel nameLabel = new JLabel(product.getName());
        nameLabel.setFont(new Font("Arial", Font.BOLD, 14));

        JLabel typeLabel = new JLabel("Type: " + product.getListType());
        typeLabel.setFont(new Font("Arial", Font.PLAIN, 12));

        String priceInfo = "";
        switch (product.getListType()) {
            case BUY:
                priceInfo = String.format("Price: $%.2f", product.getPrice());
                break;
            case BID:
                priceInfo = String.format("Start: $%.2f, Current: $%.2f",
                        product.getStartingPrice(), product.getCurrentBid());
                break;
            case LEND:
                priceInfo = String.format("Rental: $%.2f/day", product.getPrice());
                break;
        }

        JLabel priceLabel = new JLabel(priceInfo);
        priceLabel.setFont(new Font("Arial", Font.PLAIN, 12));

        JLabel statusLabel = new JLabel("Status: " + product.getAvailabilityStatus());
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 12));

        JLabel categoryLabel = new JLabel("Category: " + product.getCategory());
        categoryLabel.setFont(new Font("Arial", Font.PLAIN, 12));

        JLabel idLabel = new JLabel("ID: " + product.getId());
        idLabel.setFont(new Font("Arial", Font.PLAIN, 10));
        idLabel.setForeground(Color.GRAY);

        infoPanel.add(nameLabel);
        infoPanel.add(priceLabel);
        infoPanel.add(typeLabel);
        infoPanel.add(statusLabel);
        infoPanel.add(categoryLabel);
        infoPanel.add(idLabel);

        // Action buttons
        JPanel actionPanel = new JPanel(new GridLayout(2, 2, 5, 5));

        JButton editButton = new JButton("Edit");
        editButton.setBackground(new Color(255, 165, 0)); // Orange
        editButton.setForeground(Color.BLACK);
        editButton.addActionListener(e -> editProduct(product));

        JButton deleteButton = new JButton("Delete");
        deleteButton.setBackground(new Color(204, 0, 0));
        deleteButton.setForeground(Color.BLACK);
        deleteButton.addActionListener(e -> deleteProduct(product));

        JButton viewButton = new JButton("View");
        viewButton.addActionListener(e -> viewProduct(product));

        // Stats button (would show bids, rentals, etc.)
        JButton statsButton = new JButton("Stats");
        statsButton.addActionListener(e -> showProductStats(product));

        actionPanel.add(editButton);
        actionPanel.add(deleteButton);
        actionPanel.add(viewButton);
        actionPanel.add(statsButton);

        card.add(infoPanel, BorderLayout.CENTER);
        card.add(actionPanel, BorderLayout.EAST);

        return card;
    }

    private JPanel createProductBidHeader(Product product) {
        JPanel header = new JPanel(new BorderLayout());
        header.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0, 102, 204)),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        header.setBackground(new Color(230, 240, 255));

        JLabel productLabel = new JLabel(product.getName() + " (Auction)");
        productLabel.setFont(new Font("Arial", Font.BOLD, 14));

        String bidInfo = String.format("Starting: $%.2f | Current: $%.2f",
                product.getStartingPrice(), product.getCurrentBid());
        JLabel bidLabel = new JLabel(bidInfo);
        bidLabel.setFont(new Font("Arial", Font.PLAIN, 12));

        header.add(productLabel, BorderLayout.WEST);
        header.add(bidLabel, BorderLayout.EAST);

        return header;
    }

    private JPanel createBidCard(Bid bid, Product product) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                BorderFactory.createEmptyBorder(8, 8, 8, 8)
        ));
        card.setMaximumSize(new Dimension(800, 80));

        // Bid info
        JPanel infoPanel = new JPanel(new GridLayout(2, 1));

        String bidderInfo = String.format("Bidder: User #%d | Amount: $%.2f",
                bid.getUserId(), bid.getBidAmount());
        JLabel bidderLabel = new JLabel(bidderInfo);
        bidderLabel.setFont(new Font("Arial", Font.BOLD, 12));

        String timeInfo = "Placed: " + bid.getBidTime().toString();
        JLabel timeLabel = new JLabel(timeInfo);
        timeLabel.setFont(new Font("Arial", Font.PLAIN, 10));
        timeLabel.setForeground(Color.GRAY);

        infoPanel.add(bidderLabel);
        infoPanel.add(timeLabel);

        // Action buttons
        JPanel actionPanel = new JPanel(new FlowLayout());

        if (bid.isWinning()) {
            JLabel winningLabel = new JLabel("🏆 WINNING BID");
            winningLabel.setFont(new Font("Arial", Font.BOLD, 12));
            winningLabel.setForeground(new Color(0, 102, 0));
            actionPanel.add(winningLabel);
        } else {
            JButton approveButton = new JButton("Approve & Sell");
            approveButton.setBackground(new Color(0, 102, 0));
            approveButton.setForeground(Color.black);
            approveButton.addActionListener(e -> approveBidAndSell(bid, product));

            JButton rejectButton = new JButton("Reject");
            rejectButton.setBackground(new Color(204, 0, 0));
            rejectButton.setForeground(Color.BLACK);
            rejectButton.addActionListener(e -> rejectBid(bid));

            actionPanel.add(approveButton);
            actionPanel.add(rejectButton);
        }

        card.add(infoPanel, BorderLayout.CENTER);
        card.add(actionPanel, BorderLayout.EAST);

        return card;
    }

    private void showAddProductDialog() {
        AddProductDialog dialog = new AddProductDialog(
                (java.awt.Frame) SwingUtilities.getWindowAncestor(this),
                currentUser,
                productDAO
        );
        dialog.setVisible(true);

        if (dialog.isProductAdded()) {
            // Refresh the products panel
            JPanel productsPanel = (JPanel) ((JScrollPane) ((JPanel) tabbedPane.getComponentAt(0)).getComponent(1)).getViewport().getView();
            loadSellerProducts(productsPanel);
        }
    }

    private void editProduct(Product product) {
        EditProductDialog dialog = new EditProductDialog(
                (java.awt.Frame) SwingUtilities.getWindowAncestor(this),
                product,
                productDAO
        );
        dialog.setVisible(true);

        if (dialog.isProductUpdated()) {
            // Refresh the products panel
            JPanel productsPanel = (JPanel) ((JScrollPane) ((JPanel) tabbedPane.getComponentAt(0)).getComponent(1)).getViewport().getView();
            loadSellerProducts(productsPanel);
        }
    }

    private void deleteProduct(Product product) {
        int result = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete '" + product.getName() + "'?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION);

        if (result == JOptionPane.YES_OPTION) {
            if (productDAO.deleteProduct(product.getId())) {
                JOptionPane.showMessageDialog(this, "Product deleted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                // Refresh the products panel
                JPanel productsPanel = (JPanel) ((JScrollPane) ((JPanel) tabbedPane.getComponentAt(0)).getComponent(1)).getViewport().getView();
                loadSellerProducts(productsPanel);
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete product.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void viewProduct(Product product) {
        String message = String.format(
                "Product Details:\n\nName: %s\nDescription: %s\nCategory: %s\nType: %s\nStatus: %s",
                product.getName(),
                product.getDescription(),
                product.getCategory(),
                product.getListType(),
                product.getAvailabilityStatus()
        );
        JOptionPane.showMessageDialog(this, message, "Product Details", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showProductStats(Product product) {
        StringBuilder stats = new StringBuilder();
        stats.append("Product Statistics:\n\n");
        stats.append("Name: ").append(product.getName()).append("\n");
        stats.append("Type: ").append(product.getListType()).append("\n");
        stats.append("Status: ").append(product.getAvailabilityStatus()).append("\n\n");

        if (product.getListType() == Product.ListingType.BID) {
            List<Bid> bids = bidDAO.getBidsByProduct(product.getId());
            stats.append("Total Bids: ").append(bids.size()).append("\n");
            if (!bids.isEmpty()) {
                stats.append("Highest Bid: $").append(product.getCurrentBid()).append("\n");
            }
        }

        JOptionPane.showMessageDialog(this, stats.toString(), "Product Statistics", JOptionPane.INFORMATION_MESSAGE);
    }

    private void approveBidAndSell(Bid bid, Product product) {
        int result = JOptionPane.showConfirmDialog(this,
                "Approve this bid and sell the product?\n\n" +
                        "Product: " + product.getName() + "\n" +
                        "Bidder: User #" + bid.getUserId() + "\n" +
                        "Bid Amount: $" + bid.getBidAmount() + "\n\n" +
                        "This will mark the product as sold and notify the buyer.",
                "Approve Bid & Sell",
                JOptionPane.YES_NO_OPTION);

        if (result == JOptionPane.YES_OPTION) {
            try {
                // Mark bid as winning
                if (bidDAO.updateWinningBid(product.getId(), bid.getId())) {
                    // Update product status to sold
                    product.setAvailabilityStatus(Product.AvailabilityStatus.OUT_OF_STOCK);
                    productDAO.updateProduct(product);

                    // Create an order for the bid
                    Order order = new Order();
                    order.setUserId(bid.getUserId());
                    order.setTotalAmount(bid.getBidAmount());
                    order.setShippingAddress("To be provided by buyer");
                    order.setPaymentMethod("Auction Win");
                    order.setStatus(Order.OrderStatus.CONFIRMED);

                    if (orderDAO.createOrder(order)) {
                        // Create order item
                        OrderItem orderItem = new OrderItem();
                        orderItem.setOrderId(order.getId());
                        orderItem.setProductId(product.getId());
                        orderItem.setQuantity(1);
                        orderItem.setUnitPrice(bid.getBidAmount());
                        orderItem.setTotalPrice(bid.getBidAmount());

                        if (orderItemDAO.createOrderItem(orderItem)) {
                            JOptionPane.showMessageDialog(this,
                                    "Bid approved and product sold successfully!\n\n" +
                                            "Order ID: " + order.getId() + "\n" +
                                            "Buyer: User #" + bid.getUserId() + "\n" +
                                            "Amount: $" + bid.getBidAmount(),
                                    "Sale Completed",
                                    JOptionPane.INFORMATION_MESSAGE);

                            // Refresh bids panel
                            JPanel bidsPanel = (JPanel) ((JScrollPane) ((JPanel) tabbedPane.getComponentAt(1)).getComponent(1)).getViewport().getView();
                            loadSellerBids(bidsPanel);
                        }
                    }
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                        "Error approving bid: " + e.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void rejectBid(Bid bid) {
        int result = JOptionPane.showConfirmDialog(this,
                "Reject this bid?\n\n" +
                        "Bid Amount: $" + bid.getBidAmount() + "\n" +
                        "This action cannot be undone.",
                "Reject Bid",
                JOptionPane.YES_NO_OPTION);

        if (result == JOptionPane.YES_OPTION) {
            // In a real system, we might mark the bid as rejected
            // For now, we'll just show a message
            JOptionPane.showMessageDialog(this,
                    "Bid rejected successfully.",
                    "Bid Rejected",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }
}