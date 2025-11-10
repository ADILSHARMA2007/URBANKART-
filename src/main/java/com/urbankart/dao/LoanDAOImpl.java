package com.urbankart.dao;

import com.urbankart.db.DBConnection;
import com.urbankart.model.Loan;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class LoanDAOImpl implements LoanDAO {

    @Override
    public boolean createLoan(Loan loan) {
        String sql = "INSERT INTO Loans (product_id, user_id, start_date, end_date, total_rental_fee, status) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, loan.getProductId());
            stmt.setInt(2, loan.getUserId());
            stmt.setDate(3, Date.valueOf(loan.getStartDate()));
            stmt.setDate(4, Date.valueOf(loan.getEndDate()));
            stmt.setBigDecimal(5, loan.getTotalRentalFee());
            stmt.setString(6, loan.getStatus().name());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                // Update the product's availability if the loan is active
                if (loan.getStatus() == Loan.LoanStatus.ACTIVE) {
                    updateProductAvailability(loan.getProductId(), false);
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Error creating loan: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public List<Loan> getLoansByProduct(int productId) {
        List<Loan> loans = new ArrayList<>();
        String sql = "SELECT * FROM Loans WHERE product_id = ? ORDER BY start_date DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, productId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                loans.add(mapResultSetToLoan(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting loans by product: " + e.getMessage());
            e.printStackTrace();
        }
        return loans;
    }

    @Override
    public List<Loan> getLoansByUser(int userId) {
        List<Loan> loans = new ArrayList<>();
        String sql = "SELECT * FROM Loans WHERE user_id = ? ORDER BY start_date DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                loans.add(mapResultSetToLoan(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting loans by user: " + e.getMessage());
            e.printStackTrace();
        }
        return loans;
    }

    @Override
    public boolean isProductAvailable(int productId, LocalDate startDate, LocalDate endDate) {
        String sql = "SELECT * FROM Loans WHERE product_id = ? AND status IN ('PENDING', 'ACTIVE') AND ((start_date BETWEEN ? AND ?) OR (end_date BETWEEN ? AND ?) OR (start_date <= ? AND end_date >= ?))";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, productId);
            stmt.setDate(2, Date.valueOf(startDate));
            stmt.setDate(3, Date.valueOf(endDate));
            stmt.setDate(4, Date.valueOf(startDate));
            stmt.setDate(5, Date.valueOf(endDate));
            stmt.setDate(6, Date.valueOf(startDate));
            stmt.setDate(7, Date.valueOf(endDate));

            ResultSet rs = stmt.executeQuery();
            // If there is any overlapping loan, the product is not available
            return !rs.next();
        } catch (SQLException e) {
            System.err.println("Error checking product availability: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    private void updateProductAvailability(int productId, boolean available) {
        String sql = "UPDATE Products SET availability_status = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, available ? "AVAILABLE" : "ON_LOAN");
            stmt.setInt(2, productId);

            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error updating product availability: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private Loan mapResultSetToLoan(ResultSet rs) throws SQLException {
        Loan loan = new Loan();
        loan.setId(rs.getInt("id"));
        loan.setProductId(rs.getInt("product_id"));
        loan.setUserId(rs.getInt("user_id"));
        loan.setStartDate(rs.getDate("start_date").toLocalDate());
        loan.setEndDate(rs.getDate("end_date").toLocalDate());
        loan.setTotalRentalFee(rs.getBigDecimal("total_rental_fee"));
        loan.setStatus(Loan.LoanStatus.valueOf(rs.getString("status")));
        loan.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        return loan;
    }
}