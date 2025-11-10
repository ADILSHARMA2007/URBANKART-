package com.urbankart.ui;

import com.urbankart.model.User;

import javax.swing.*;
import java.awt.*;

public class MainDashboardFrame extends JFrame {
    private User currentUser;
    private JTabbedPane tabbedPane;

    public MainDashboardFrame(User user) {
        this.currentUser = user;
        initializeUI();
    }

    private void initializeUI() {
        setTitle("UrbanKart - Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);

        // Create menu bar
        createMenuBar();

        // Create main content
        tabbedPane = new JTabbedPane();

        // Add change listener to refresh panels when tabs are selected
        tabbedPane.addChangeListener(e -> {
            int selectedIndex = tabbedPane.getSelectedIndex();
            if (selectedIndex != -1) {
                Component selectedComponent = tabbedPane.getComponentAt(selectedIndex);
                String tabTitle = tabbedPane.getTitleAt(selectedIndex);
                System.out.println("📑 Tab selected: " + tabTitle);

                // Refresh cart panel when selected
                if (selectedComponent instanceof CartPanel) {
                    System.out.println("🔄 Refreshing CartPanel...");
                    ((CartPanel) selectedComponent).refreshCart();
                }
            }
        });

        // Add tabs based on user role
        tabbedPane.addTab("Browse Products", new ProductBrowserPanel(currentUser));

        // Only show cart, bids, rentals for BUYER role
        if (currentUser.getRole() == User.UserRole.BUYER) {
            tabbedPane.addTab("My Cart", new CartPanel(currentUser));
            tabbedPane.addTab("My Bids", new MyBidsPanel(currentUser));
            tabbedPane.addTab("My Rentals", new MyRentalsPanel(currentUser));
        }

        if (currentUser.getRole() == User.UserRole.SELLER || currentUser.getRole() == User.UserRole.ADMIN) {
            tabbedPane.addTab("Seller Dashboard", new SellerDashboardPanel(currentUser));
        }

        if (currentUser.getRole() == User.UserRole.ADMIN) {
            tabbedPane.addTab("Admin Dashboard", new AdminDashboardPanel(currentUser));
        }

        add(tabbedPane);
    }

    private void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        // File menu
        JMenu fileMenu = new JMenu("File");
        JMenuItem logoutItem = new JMenuItem("Logout");
        JMenuItem exitItem = new JMenuItem("Exit");

        logoutItem.addActionListener(e -> logout());
        exitItem.addActionListener(e -> System.exit(0));

        fileMenu.add(logoutItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);

        // User menu
        JMenu userMenu = new JMenu("User");
        JMenuItem profileItem = new JMenuItem("My Profile");
        JMenuItem notificationsItem = new JMenuItem("Notifications");

        profileItem.addActionListener(e -> showProfile());
        notificationsItem.addActionListener(e -> showNotifications());

        userMenu.add(profileItem);
        userMenu.add(notificationsItem);

        menuBar.add(fileMenu);
        menuBar.add(userMenu);

        setJMenuBar(menuBar);
    }

    private void logout() {
        int result = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to logout?",
                "Logout",
                JOptionPane.YES_NO_OPTION);

        if (result == JOptionPane.YES_OPTION) {
            new LoginFrame().setVisible(true);
            dispose();
        }
    }

    private void showProfile() {
        String message = String.format(
                "User Profile:\n\nUsername: %s\nEmail: %s\nRole: %s",
                currentUser.getUsername(),
                currentUser.getEmail(),
                currentUser.getRole()
        );
        JOptionPane.showMessageDialog(this, message, "My Profile", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showNotifications() {
        // This would open a notifications panel
        JOptionPane.showMessageDialog(this,
                "Notifications feature to be implemented",
                "Notifications",
                JOptionPane.INFORMATION_MESSAGE);
    }
}