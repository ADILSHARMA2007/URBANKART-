package com.urbankart.dao;

import com.urbankart.db.DBConnection;
import com.urbankart.model.Bid;
import com.urbankart.model.BidDetail;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BidDAOImpl implements BidDAO {

    @Override
    public boolean placeBid(Bid bid) {
        String sql = "INSERT INTO Bids (product_id, user_id, bid_amount) VALUES (?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, bid.getProductId());
            stmt.setInt(2, bid.getUserId());
            stmt.setBigDecimal(3, bid.getBidAmount());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                // Get the generated bid ID
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        bid.setId(generatedKeys.getInt(1));
                    }
                }

                // Update product's current bid
                updateProductCurrentBid(bid.getProductId(), bid.getBidAmount(), bid.getUserId());

                System.out.println("✅ Bid placed successfully - Product: " + bid.getProductId() +
                        ", User: " + bid.getUserId() + ", Amount: " + bid.getBidAmount());
                return true;
            }
        } catch (SQLException e) {
            System.err.println("❌ Error placing bid: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public Bid getHighestBid(int productId) {
        String sql = "SELECT * FROM Bids WHERE product_id = ? ORDER BY bid_amount DESC LIMIT 1";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, productId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToBid(rs);
            }
        } catch (SQLException e) {
            System.err.println("❌ Error getting highest bid: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Bid> getBidsByProduct(int productId) {
        List<Bid> bids = new ArrayList<>();
        String sql = "SELECT b.*, u.username FROM Bids b JOIN Users u ON b.user_id = u.id WHERE product_id = ? ORDER BY bid_amount DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, productId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Bid bid = mapResultSetToBid(rs);
                bids.add(bid);
            }
        } catch (SQLException e) {
            System.err.println("❌ Error getting bids by product: " + e.getMessage());
            e.printStackTrace();
        }
        return bids;
    }

    @Override
    public List<Bid> getBidsByUser(int userId) {
        List<Bid> bids = new ArrayList<>();
        String sql = "SELECT b.*, p.name as product_name FROM Bids b JOIN Products p ON b.product_id = p.id WHERE user_id = ? ORDER BY bid_time DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Bid bid = mapResultSetToBid(rs);
                bids.add(bid);
            }
        } catch (SQLException e) {
            System.err.println("❌ Error getting bids by user: " + e.getMessage());
            e.printStackTrace();
        }
        return bids;
    }

    @Override
    public boolean updateWinningBid(int productId, int winningBidId) {
        // First, reset all bids for this product to not winning
        String resetSql = "UPDATE Bids SET is_winning = false WHERE product_id = ?";
        String setWinningSql = "UPDATE Bids SET is_winning = true WHERE id = ?";

        try (Connection conn = DBConnection.getConnection()) {
            // Reset all bids
            try (PreparedStatement resetStmt = conn.prepareStatement(resetSql)) {
                resetStmt.setInt(1, productId);
                resetStmt.executeUpdate();
            }

            // Set winning bid
            try (PreparedStatement winningStmt = conn.prepareStatement(setWinningSql)) {
                winningStmt.setInt(1, winningBidId);
                int affected = winningStmt.executeUpdate();
                return affected > 0;
            }
        } catch (SQLException e) {
            System.err.println("❌ Error updating winning bid: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public Bid getBidById(int bidId) {
        String sql = "SELECT * FROM Bids WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, bidId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToBid(rs);
            }
        } catch (SQLException e) {
            System.err.println("❌ Error getting bid by ID: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    // Remove the updateProductCurrentBid method that was causing the issue
    // and replace it with this one:
    private void updateProductCurrentBid(int productId, java.math.BigDecimal newBid, int bidderId) {
        String sql = "UPDATE Products SET current_bid = ?, current_bidder_id = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setBigDecimal(1, newBid);
            stmt.setInt(2, bidderId);
            stmt.setInt(3, productId);

            stmt.executeUpdate();
            System.out.println("✅ Updated product current bid - Product: " + productId +
                    ", New Bid: " + newBid + ", Bidder: " + bidderId);
        } catch (SQLException e) {
            System.err.println("❌ Error updating product current bid: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private Bid mapResultSetToBid(ResultSet rs) throws SQLException {
        Bid bid = new Bid();
        bid.setId(rs.getInt("id"));
        bid.setProductId(rs.getInt("product_id"));
        bid.setUserId(rs.getInt("user_id"));
        bid.setBidAmount(rs.getBigDecimal("bid_amount"));
        bid.setBidTime(rs.getTimestamp("bid_time").toLocalDateTime());
        bid.setWinning(rs.getBoolean("is_winning"));
        return bid;
    }

    // Remove the problematic methods that reference non-existent classes
    // Remove getBidsBySeller and getBidDetailsBySeller methods
}