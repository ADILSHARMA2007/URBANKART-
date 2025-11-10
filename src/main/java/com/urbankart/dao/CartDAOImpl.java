package com.urbankart.dao;

import com.urbankart.db.DBConnection;
import com.urbankart.model.CartItem;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CartDAOImpl implements CartDAO {

    @Override
    public List<CartItem> getCartItemsByUser(int userId) {
        List<CartItem> cartItems = new ArrayList<>();
        String sql = "SELECT ci.*, p.name, p.price, p.list_type " +
                "FROM CartItems ci " +
                "JOIN Products p ON ci.product_id = p.id " +
                "WHERE ci.user_id = ?";

        System.out.println("=== DEBUG: Getting cart items for user " + userId + " ===");
        System.out.println("SQL: " + sql);

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            int count = 0;
            while (rs.next()) {
                count++;
                CartItem item = mapResultSetToCartItem(rs);
                cartItems.add(item);
                System.out.println("✅ Cart Item - ID: " + item.getId() +
                        ", Product: " + item.getProductId() +
                        ", Qty: " + item.getQuantity());
            }
            System.out.println("📊 Total cart items retrieved: " + count);

        } catch (SQLException e) {
            System.err.println("❌ ERROR in getCartItemsByUser: " + e.getMessage());
            System.err.println("SQL State: " + e.getSQLState());
            System.err.println("Error Code: " + e.getErrorCode());
            e.printStackTrace();
        }

        System.out.println("=== END DEBUG ===");
        return cartItems;
    }

    @Override
    public boolean addToCart(int userId, int productId, int quantity) {
        System.out.println("=== DEBUG: Adding to cart ===");
        System.out.println("User ID: " + userId);
        System.out.println("Product ID: " + productId);
        System.out.println("Quantity: " + quantity);

        // Check if item already exists in cart
        CartItem existingItem = getCartItem(userId, productId);

        if (existingItem != null) {
            System.out.println("🔄 Item exists, updating quantity from " +
                    existingItem.getQuantity() + " to " +
                    (existingItem.getQuantity() + quantity));
            return updateCartItemQuantity(existingItem.getId(), existingItem.getQuantity() + quantity);
        } else {
            // Add new item
            String sql = "INSERT INTO CartItems (user_id, product_id, quantity) VALUES (?, ?, ?)";
            System.out.println("SQL: " + sql);

            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {

                stmt.setInt(1, userId);
                stmt.setInt(2, productId);
                stmt.setInt(3, quantity);

                int rowsAffected = stmt.executeUpdate();
                boolean success = rowsAffected > 0;

                System.out.println("Rows affected: " + rowsAffected);
                System.out.println("Success: " + success);

                if (success) {
                    System.out.println("✅ SUCCESS: Added to cart");
                } else {
                    System.out.println("❌ FAILED: No rows affected");
                }

                return success;
            } catch (SQLException e) {
                System.err.println("❌ SQL ERROR in addToCart: " + e.getMessage());
                System.err.println("SQL State: " + e.getSQLState());
                System.err.println("Error Code: " + e.getErrorCode());
                e.printStackTrace();
            }
        }
        return false;
    }

    @Override
    public boolean updateCartItemQuantity(int cartItemId, int quantity) {
        System.out.println("=== DEBUG: Updating cart item quantity ===");
        System.out.println("Cart Item ID: " + cartItemId);
        System.out.println("New Quantity: " + quantity);

        if (quantity <= 0) {
            System.out.println("🔄 Quantity <= 0, removing item");
            return removeFromCart(cartItemId);
        }

        String sql = "UPDATE CartItems SET quantity = ? WHERE id = ?";
        System.out.println("SQL: " + sql);

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, quantity);
            stmt.setInt(2, cartItemId);

            int rowsAffected = stmt.executeUpdate();
            boolean success = rowsAffected > 0;

            System.out.println("Rows affected: " + rowsAffected);
            System.out.println("Success: " + success);

            return success;
        } catch (SQLException e) {
            System.err.println("❌ ERROR in updateCartItemQuantity: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean removeFromCart(int cartItemId) {
        System.out.println("=== DEBUG: Removing from cart ===");
        System.out.println("Cart Item ID: " + cartItemId);

        String sql = "DELETE FROM CartItems WHERE id = ?";
        System.out.println("SQL: " + sql);

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, cartItemId);
            int rowsAffected = stmt.executeUpdate();
            boolean success = rowsAffected > 0;

            System.out.println("Rows affected: " + rowsAffected);
            System.out.println("Success: " + success);

            return success;
        } catch (SQLException e) {
            System.err.println("❌ ERROR in removeFromCart: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean clearCart(int userId) {
        System.out.println("=== DEBUG: Clearing cart ===");
        System.out.println("User ID: " + userId);

        String sql = "DELETE FROM CartItems WHERE user_id = ?";
        System.out.println("SQL: " + sql);

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            int rowsDeleted = stmt.executeUpdate();

            System.out.println("Rows deleted: " + rowsDeleted);
            System.out.println("✅ Cart cleared for user: " + userId);

            return true;
        } catch (SQLException e) {
            System.err.println("❌ ERROR in clearCart: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public CartItem getCartItem(int userId, int productId) {
        System.out.println("=== DEBUG: Getting specific cart item ===");
        System.out.println("User ID: " + userId + ", Product ID: " + productId);

        String sql = "SELECT * FROM CartItems WHERE user_id = ? AND product_id = ?";
        System.out.println("SQL: " + sql);

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            stmt.setInt(2, productId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                CartItem item = mapResultSetToCartItem(rs);
                System.out.println("✅ Found existing cart item: ID=" + item.getId() + ", Qty=" + item.getQuantity());
                return item;
            } else {
                System.out.println("ℹ️ No existing cart item found");
            }
        } catch (SQLException e) {
            System.err.println("❌ ERROR in getCartItem: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    private CartItem mapResultSetToCartItem(ResultSet rs) throws SQLException {
        CartItem item = new CartItem();
        item.setId(rs.getInt("id"));
        item.setUserId(rs.getInt("user_id"));
        item.setProductId(rs.getInt("product_id"));
        item.setQuantity(rs.getInt("quantity"));
        item.setAddedAt(rs.getTimestamp("added_at").toLocalDateTime());
        return item;
    }
}