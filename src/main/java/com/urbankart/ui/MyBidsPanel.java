package com.urbankart.ui;

import com.urbankart.dao.BidDAO;
import com.urbankart.dao.BidDAOImpl;
import com.urbankart.dao.ProductDAO;
import com.urbankart.dao.ProductDAOImpl;
import com.urbankart.model.Bid;
import com.urbankart.model.User;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class MyBidsPanel extends JPanel {
    private User currentUser;
    private BidDAO bidDAO;
    private ProductDAO productDAO;
    private JPanel bidsPanel;

    public MyBidsPanel(User user) {
        this.currentUser = user;
        this.bidDAO = new BidDAOImpl();
        this.productDAO = new ProductDAOImpl();
        initializeUI();
        loadUserBids();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());

        // Header
        JLabel headerLabel = new JLabel("My Bids", SwingConstants.CENTER);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 18));
        add(headerLabel, BorderLayout.NORTH);

        // Bids panel
        bidsPanel = new JPanel();
        bidsPanel.setLayout(new BoxLayout(bidsPanel, BoxLayout.Y_AXIS));
        JScrollPane scrollPane = new JScrollPane(bidsPanel);

        add(scrollPane, BorderLayout.CENTER);
    }

    private void loadUserBids() {
        List<Bid> bids = bidDAO.getBidsByUser(currentUser.getId());
        displayBids(bids);
    }

    private void displayBids(List<Bid> bids) {
        bidsPanel.removeAll();

        if (bids.isEmpty()) {
            JLabel noBidsLabel = new JLabel("You haven't placed any bids yet.", SwingConstants.CENTER);
            noBidsLabel.setFont(new Font("Arial", Font.ITALIC, 16));
            bidsPanel.add(noBidsLabel);
        } else {
            for (Bid bid : bids) {
                bidsPanel.add(createBidCard(bid));
                bidsPanel.add(Box.createRigidArea(new Dimension(0, 10)));
            }
        }

        bidsPanel.revalidate();
        bidsPanel.repaint();
    }

    private JPanel createBidCard(Bid bid) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        card.setMaximumSize(new Dimension(800, 80));

        // Get product details
        var product = productDAO.getProductById(bid.getProductId());
        String productName = product != null ? product.getName() : "Product #" + bid.getProductId();

        // Bid info
        JPanel infoPanel = new JPanel(new GridLayout(2, 1));
        JLabel productLabel = new JLabel(productName);
        productLabel.setFont(new Font("Arial", Font.BOLD, 14));

        String bidInfo = String.format("Your Bid: $%.2f - Placed: %s",
                bid.getBidAmount(), bid.getBidTime().toString());
        JLabel bidLabel = new JLabel(bidInfo);
        bidLabel.setFont(new Font("Arial", Font.PLAIN, 12));

        infoPanel.add(productLabel);
        infoPanel.add(bidLabel);

        // Status
        JLabel statusLabel = new JLabel(bid.isWinning() ? "🏆 WINNING BID" : "Outbid");
        statusLabel.setFont(new Font("Arial", Font.BOLD, 12));
        statusLabel.setForeground(bid.isWinning() ? new Color(0, 102, 0) : Color.RED);

        card.add(infoPanel, BorderLayout.CENTER);
        card.add(statusLabel, BorderLayout.EAST);

        return card;
    }
}