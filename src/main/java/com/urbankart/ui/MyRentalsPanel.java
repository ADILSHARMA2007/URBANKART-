package com.urbankart.ui;

import com.urbankart.dao.LoanDAO;
import com.urbankart.dao.LoanDAOImpl;
import com.urbankart.dao.ProductDAO;
import com.urbankart.dao.ProductDAOImpl;
import com.urbankart.model.Loan;
import com.urbankart.model.User;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class MyRentalsPanel extends JPanel {
    private User currentUser;
    private LoanDAO loanDAO;
    private ProductDAO productDAO;
    private JPanel rentalsPanel;

    public MyRentalsPanel(User user) {
        this.currentUser = user;
        this.loanDAO = new LoanDAOImpl();
        this.productDAO = new ProductDAOImpl();
        initializeUI();
        loadUserRentals();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());

        // Header
        JLabel headerLabel = new JLabel("My Rentals", SwingConstants.CENTER);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 18));
        add(headerLabel, BorderLayout.NORTH);

        // Rentals panel
        rentalsPanel = new JPanel();
        rentalsPanel.setLayout(new BoxLayout(rentalsPanel, BoxLayout.Y_AXIS));
        JScrollPane scrollPane = new JScrollPane(rentalsPanel);

        add(scrollPane, BorderLayout.CENTER);
    }

    private void loadUserRentals() {
        List<Loan> loans = loanDAO.getLoansByUser(currentUser.getId());
        displayRentals(loans);
    }

    private void displayRentals(List<Loan> loans) {
        rentalsPanel.removeAll();

        if (loans.isEmpty()) {
            JLabel noRentalsLabel = new JLabel("You don't have any rentals.", SwingConstants.CENTER);
            noRentalsLabel.setFont(new Font("Arial", Font.ITALIC, 16));
            rentalsPanel.add(noRentalsLabel);
        } else {
            for (Loan loan : loans) {
                rentalsPanel.add(createRentalCard(loan));
                rentalsPanel.add(Box.createRigidArea(new Dimension(0, 10)));
            }
        }

        rentalsPanel.revalidate();
        rentalsPanel.repaint();
    }

    private JPanel createRentalCard(Loan loan) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        card.setMaximumSize(new Dimension(800, 100));

        // Get product details
        var product = productDAO.getProductById(loan.getProductId());
        String productName = product != null ? product.getName() : "Product #" + loan.getProductId();

        // Rental info
        JPanel infoPanel = new JPanel(new GridLayout(3, 1));
        JLabel productLabel = new JLabel(productName);
        productLabel.setFont(new Font("Arial", Font.BOLD, 14));

        String periodInfo = String.format("Period: %s to %s",
                loan.getStartDate(), loan.getEndDate());
        JLabel periodLabel = new JLabel(periodInfo);
        periodLabel.setFont(new Font("Arial", Font.PLAIN, 12));

        String costInfo = String.format("Total Cost: $%.2f", loan.getTotalRentalFee());
        JLabel costLabel = new JLabel(costInfo);
        costLabel.setFont(new Font("Arial", Font.PLAIN, 12));

        infoPanel.add(productLabel);
        infoPanel.add(periodLabel);
        infoPanel.add(costLabel);

        // Status
        JLabel statusLabel = new JLabel("Status: " + loan.getStatus());
        statusLabel.setFont(new Font("Arial", Font.BOLD, 12));

        Color statusColor;
        switch (loan.getStatus()) {
            case PENDING: statusColor = Color.ORANGE; break;
            case ACTIVE: statusColor = new Color(0, 102, 0); break;
            case COMPLETED: statusColor = Color.BLUE; break;
            case CANCELLED: statusColor = Color.RED; break;
            default: statusColor = Color.BLACK;
        }
        statusLabel.setForeground(statusColor);

        card.add(infoPanel, BorderLayout.CENTER);
        card.add(statusLabel, BorderLayout.EAST);

        return card;
    }
}