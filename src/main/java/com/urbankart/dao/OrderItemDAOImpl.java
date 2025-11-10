package com.urbankart.dao;

import com.urbankart.db.DBConnection;
import com.urbankart.model.OrderItem;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrderItemDAOImpl implements OrderItemDAO {

    @Override
    public List<OrderItem> getOrderItemsByOrder(int orderId) {
        List<OrderItem> orderItems = new ArrayList<>();
        String sql = "SELECT oi.*, p.name as product_name FROM OrderItems oi " +
                "JOIN Products p ON oi.product_id = p.id " +
                "WHERE oi.order_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, orderId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                orderItems.add(mapResultSetToOrderItem(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting order items: " + e.getMessage());
        }
        return orderItems;
    }

    @Override
    public boolean createOrderItem(OrderItem orderItem) {
        String sql = "INSERT INTO OrderItems (order_id, product_id, quantity, unit_price, total_price) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, orderItem.getOrderId());
            stmt.setInt(2, orderItem.getProductId());
            stmt.setInt(3, orderItem.getQuantity());
            stmt.setBigDecimal(4, orderItem.getUnitPrice());
            stmt.setBigDecimal(5, orderItem.getTotalPrice());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error creating order item: " + e.getMessage());
        }
        return false;
    }

    private OrderItem mapResultSetToOrderItem(ResultSet rs) throws SQLException {
        OrderItem item = new OrderItem();
        item.setId(rs.getInt("id"));
        item.setOrderId(rs.getInt("order_id"));
        item.setProductId(rs.getInt("product_id"));
        item.setQuantity(rs.getInt("quantity"));
        item.setUnitPrice(rs.getBigDecimal("unit_price"));
        item.setTotalPrice(rs.getBigDecimal("total_price"));
        return item;
    }
}