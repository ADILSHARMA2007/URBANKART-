package com.urbankart.dao;

import com.urbankart.db.DBConnection;
import com.urbankart.model.Order;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrderDAOImpl implements OrderDAO {

    @Override
    public Order getOrderById(int id) {
        String sql = "SELECT * FROM Orders WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToOrder(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error getting order by ID: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Order> getOrdersByUser(int userId) {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT * FROM Orders WHERE user_id = ? ORDER BY order_date DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                orders.add(mapResultSetToOrder(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting orders by user: " + e.getMessage());
            e.printStackTrace();
        }
        return orders;
    }

    @Override
    public boolean createOrder(Order order) {
        String sql = "INSERT INTO Orders (user_id, total_amount, shipping_address, payment_method, status) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, order.getUserId());
            stmt.setBigDecimal(2, order.getTotalAmount());
            stmt.setString(3, order.getShippingAddress());
            stmt.setString(4, order.getPaymentMethod());
            stmt.setString(5, order.getStatus().name());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating order failed, no rows affected.");
            }

            // Get the generated order ID
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    order.setId(generatedKeys.getInt(1));
                    System.out.println("✅ Order created with ID: " + order.getId());
                    return true;
                } else {
                    throw new SQLException("Creating order failed, no ID obtained.");
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Error creating order: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean updateOrderStatus(int orderId, Order.OrderStatus status) {
        String sql = "UPDATE Orders SET status = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, status.name());
            stmt.setInt(2, orderId);

            int rowsAffected = stmt.executeUpdate();
            System.out.println("✅ Updated order " + orderId + " status to: " + status);
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("❌ Error updating order status: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    private Order mapResultSetToOrder(ResultSet rs) throws SQLException {
        Order order = new Order();
        order.setId(rs.getInt("id"));
        order.setUserId(rs.getInt("user_id"));
        order.setOrderDate(rs.getTimestamp("order_date").toLocalDateTime());
        order.setTotalAmount(rs.getBigDecimal("total_amount"));
        order.setStatus(Order.OrderStatus.valueOf(rs.getString("status")));
        order.setShippingAddress(rs.getString("shipping_address"));
        order.setPaymentMethod(rs.getString("payment_method"));
        return order;
    }
}