package com.urbankart.ui;

import com.urbankart.dao.UserDAO;
import com.urbankart.dao.UserDAOImpl;
import com.urbankart.dao.ProductDAO;
import com.urbankart.dao.ProductDAOImpl;
import com.urbankart.model.User;
import com.urbankart.model.Product;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class AdminDashboardPanel extends JPanel {
    private User currentUser;
    private UserDAO userDAO;
    private ProductDAO productDAO;
    private JTabbedPane tabbedPane;

    public AdminDashboardPanel(User user) {
        this.currentUser = user;
        this.userDAO = new UserDAOImpl();
        this.productDAO = new ProductDAOImpl();
        initializeUI();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());

        // Header
        JLabel headerLabel = new JLabel("Admin Dashboard", SwingConstants.CENTER);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 18));
        headerLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Tabbed pane for different admin sections
        tabbedPane = new JTabbedPane();
        tabbedPane.addTab("User Management", createUserManagementPanel());
        tabbedPane.addTab("Product Management", createProductManagementPanel());
        tabbedPane.addTab("System Overview", createSystemOverviewPanel());

        add(headerLabel, BorderLayout.NORTH);
        add(tabbedPane, BorderLayout.CENTER);
    }

    private JPanel createUserManagementPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // Header with refresh button
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> refreshUserTable());

        headerPanel.add(refreshButton);

        // User table
        List<User> users = userDAO.getAllUsers();
        String[] columnNames = {"ID", "Username", "Email", "Role", "Status"};
        Object[][] data = new Object[users.size()][5];

        for (int i = 0; i < users.size(); i++) {
            User user = users.get(i);
            data[i][0] = user.getId();
            data[i][1] = user.getUsername();
            data[i][2] = user.getEmail();
            data[i][3] = user.getRole();
            data[i][4] = user.isActive() ? "Active" : "Inactive";
        }

        JTable userTable = new JTable(data, columnNames);
        JScrollPane scrollPane = new JScrollPane(userTable);

        // Action buttons
        JPanel actionPanel = new JPanel(new FlowLayout());
        JButton editUserButton = new JButton("Edit User");
        JButton toggleStatusButton = new JButton("Toggle Status");
        JButton deleteUserButton = new JButton("Delete User");

        editUserButton.addActionListener(e -> editSelectedUser(userTable));
        toggleStatusButton.addActionListener(e -> toggleUserStatus(userTable));
        deleteUserButton.addActionListener(e -> deleteSelectedUser(userTable));

        actionPanel.add(editUserButton);
        actionPanel.add(toggleStatusButton);
        actionPanel.add(deleteUserButton);

        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(actionPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createProductManagementPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        JLabel infoLabel = new JLabel("All Products Management", SwingConstants.CENTER);
        infoLabel.setFont(new Font("Arial", Font.BOLD, 14));

        List<Product> products = productDAO.getAllProducts();
        String[] columnNames = {"ID", "Name", "Seller ID", "Type", "Price", "Status"};
        Object[][] data = new Object[products.size()][6];

        for (int i = 0; i < products.size(); i++) {
            Product product = products.get(i);
            data[i][0] = product.getId();
            data[i][1] = product.getName();
            data[i][2] = product.getSellerId();
            data[i][3] = product.getListType();

            String priceInfo = "";
            switch (product.getListType()) {
                case BUY:
                case LEND:
                    priceInfo = String.format("$%.2f", product.getPrice());
                    break;
                case BID:
                    priceInfo = String.format("$%.2f (Start: $%.2f)", product.getCurrentBid(), product.getStartingPrice());
                    break;
            }

            data[i][4] = priceInfo;
            data[i][5] = product.getAvailabilityStatus();
        }

        JTable productTable = new JTable(data, columnNames);
        JScrollPane scrollPane = new JScrollPane(productTable);

        JPanel actionPanel = new JPanel(new FlowLayout());
        JButton editProductButton = new JButton("Edit Product");
        JButton deleteProductButton = new JButton("Delete Product");

        editProductButton.addActionListener(e -> editSelectedProduct(productTable));
        deleteProductButton.addActionListener(e -> deleteSelectedProduct(productTable));

        actionPanel.add(editProductButton);
        actionPanel.add(deleteProductButton);

        panel.add(infoLabel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(actionPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createSystemOverviewPanel() {
        JPanel panel = new JPanel(new GridLayout(4, 1, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        List<User> users = userDAO.getAllUsers();
        List<Product> products = productDAO.getAllProducts();

        long activeUsers = users.stream().filter(User::isActive).count();
        long sellers = users.stream().filter(u -> u.getRole() == User.UserRole.SELLER).count();
        long buyers = users.stream().filter(u -> u.getRole() == User.UserRole.BUYER).count();

        JLabel usersLabel = new JLabel(String.format("Total Users: %d (Active: %d)", users.size(), activeUsers));
        JLabel rolesLabel = new JLabel(String.format("Sellers: %d, Buyers: %d", sellers, buyers));
        JLabel productsLabel = new JLabel(String.format("Total Products: %d", products.size()));
        JLabel adminLabel = new JLabel("System Administration Panel");

        usersLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        rolesLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        productsLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        adminLabel.setFont(new Font("Arial", Font.BOLD, 16));

        panel.add(adminLabel);
        panel.add(usersLabel);
        panel.add(rolesLabel);
        panel.add(productsLabel);

        return panel;
    }

    private void refreshUserTable() {
        JOptionPane.showMessageDialog(this, "Refresh functionality to be implemented.", "Info", JOptionPane.INFORMATION_MESSAGE);
    }

    private void editSelectedUser(JTable userTable) {
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a user to edit.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        JOptionPane.showMessageDialog(this, "Edit user functionality to be implemented.", "Info", JOptionPane.INFORMATION_MESSAGE);
    }

    private void toggleUserStatus(JTable userTable) {
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a user.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        JOptionPane.showMessageDialog(this, "Toggle status functionality to be implemented.", "Info", JOptionPane.INFORMATION_MESSAGE);
    }

    private void deleteSelectedUser(JTable userTable) {
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a user to delete.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int userId = (Integer) userTable.getValueAt(selectedRow, 0);
        String username = (String) userTable.getValueAt(selectedRow, 1);

        int result = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete user '" + username + "'?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION);

        if (result == JOptionPane.YES_OPTION) {
            if (userDAO.deleteUser(userId)) {
                JOptionPane.showMessageDialog(this, "User deleted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                // Refresh the table
                tabbedPane.setComponentAt(0, createUserManagementPanel());
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete user.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void editSelectedProduct(JTable productTable) {
        int selectedRow = productTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a product to edit.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        JOptionPane.showMessageDialog(this, "Edit product functionality to be implemented.", "Info", JOptionPane.INFORMATION_MESSAGE);
    }

    private void deleteSelectedProduct(JTable productTable) {
        int selectedRow = productTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a product to delete.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int productId = (Integer) productTable.getValueAt(selectedRow, 0);
        String productName = (String) productTable.getValueAt(selectedRow, 1);

        int result = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete product '" + productName + "'?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION);

        if (result == JOptionPane.YES_OPTION) {
            if (productDAO.deleteProduct(productId)) {
                JOptionPane.showMessageDialog(this, "Product deleted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                // Refresh the table
                tabbedPane.setComponentAt(1, createProductManagementPanel());
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete product.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}