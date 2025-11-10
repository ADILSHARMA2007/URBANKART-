package com.urbankart.dao;

import com.urbankart.db.DBConnection;
import com.urbankart.model.Product;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductDAOImpl implements ProductDAO {

    @Override
    public Product getProductById(int id) {
        String sql = "SELECT * FROM Products WHERE id = ? AND is_active = true";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToProduct(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Product> getAllProducts() {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT * FROM Products WHERE is_active = true ORDER BY created_at DESC";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                products.add(mapResultSetToProduct(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return products;
    }

    @Override
    public List<Product> getProductsBySeller(int sellerId) {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT * FROM Products WHERE seller_id = ? AND is_active = true ORDER BY created_at DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, sellerId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                products.add(mapResultSetToProduct(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return products;
    }

    @Override
    public List<Product> getProductsByCategory(String category) {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT * FROM Products WHERE category = ? AND is_active = true ORDER BY created_at DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, category);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                products.add(mapResultSetToProduct(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return products;
    }

    @Override
    public List<Product> searchProducts(String query) {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT * FROM Products WHERE (name LIKE ? OR description LIKE ?) AND is_active = true";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            String searchTerm = "%" + query + "%";
            stmt.setString(1, searchTerm);
            stmt.setString(2, searchTerm);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                products.add(mapResultSetToProduct(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return products;
    }

    @Override
    public List<Product> getProductsByType(Product.ListingType type) {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT * FROM Products WHERE list_type = ? AND is_active = true ORDER BY created_at DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, type.name());
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                products.add(mapResultSetToProduct(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return products;
    }

    @Override
    public boolean createProduct(Product product) {
        String sql = "INSERT INTO Products (seller_id, name, description, category, image_path, list_type, " +
                "price, starting_price, current_bid, auction_end_time, availability_status) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, product.getSellerId());
            stmt.setString(2, product.getName());
            stmt.setString(3, product.getDescription());
            stmt.setString(4, product.getCategory());
            stmt.setString(5, product.getImagePath());
            stmt.setString(6, product.getListType().name());

            if (product.getPrice() != null) {
                stmt.setBigDecimal(7, product.getPrice());
            } else {
                stmt.setNull(7, Types.DECIMAL);
            }

            if (product.getStartingPrice() != null) {
                stmt.setBigDecimal(8, product.getStartingPrice());
            } else {
                stmt.setNull(8, Types.DECIMAL);
            }

            if (product.getCurrentBid() != null) {
                stmt.setBigDecimal(9, product.getCurrentBid());
            } else {
                stmt.setNull(9, Types.DECIMAL);
            }

            if (product.getAuctionEndTime() != null) {
                stmt.setTimestamp(10, Timestamp.valueOf(product.getAuctionEndTime()));
            } else {
                stmt.setNull(10, Types.TIMESTAMP);
            }

            stmt.setString(11, product.getAvailabilityStatus().name());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean updateProduct(Product product) {
        String sql = "UPDATE Products SET name = ?, description = ?, category = ?, image_path = ?, " +
                "list_type = ?, price = ?, starting_price = ?, current_bid = ?, " +
                "auction_end_time = ?, availability_status = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, product.getName());
            stmt.setString(2, product.getDescription());
            stmt.setString(3, product.getCategory());
            stmt.setString(4, product.getImagePath());
            stmt.setString(5, product.getListType().name());

            if (product.getPrice() != null) {
                stmt.setBigDecimal(6, product.getPrice());
            } else {
                stmt.setNull(6, Types.DECIMAL);
            }

            if (product.getStartingPrice() != null) {
                stmt.setBigDecimal(7, product.getStartingPrice());
            } else {
                stmt.setNull(7, Types.DECIMAL);
            }

            if (product.getCurrentBid() != null) {
                stmt.setBigDecimal(8, product.getCurrentBid());
            } else {
                stmt.setNull(8, Types.DECIMAL);
            }

            if (product.getAuctionEndTime() != null) {
                stmt.setTimestamp(9, Timestamp.valueOf(product.getAuctionEndTime()));
            } else {
                stmt.setNull(9, Types.TIMESTAMP);
            }

            stmt.setString(10, product.getAvailabilityStatus().name());
            stmt.setInt(11, product.getId());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean deleteProduct(int id) {
        String sql = "UPDATE Products SET is_active = false WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean updateProductPrice(int productId, java.math.BigDecimal newPrice) {
        String sql = "UPDATE Products SET price = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setBigDecimal(1, newPrice);
            stmt.setInt(2, productId);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private Product mapResultSetToProduct(ResultSet rs) throws SQLException {
        Product product = new Product();
        product.setId(rs.getInt("id"));
        product.setSellerId(rs.getInt("seller_id"));
        product.setName(rs.getString("name"));
        product.setDescription(rs.getString("description"));
        product.setCategory(rs.getString("category"));
        product.setImagePath(rs.getString("image_path"));
        product.setListType(Product.ListingType.valueOf(rs.getString("list_type")));

        BigDecimal price = rs.getBigDecimal("price");
        if (!rs.wasNull()) product.setPrice(price);

        BigDecimal startingPrice = rs.getBigDecimal("starting_price");
        if (!rs.wasNull()) product.setStartingPrice(startingPrice);

        BigDecimal currentBid = rs.getBigDecimal("current_bid");
        if (!rs.wasNull()) product.setCurrentBid(currentBid);

        int currentBidderId = rs.getInt("current_bidder_id");
        if (!rs.wasNull()) product.setCurrentBidderId(currentBidderId);

        Timestamp auctionEndTime = rs.getTimestamp("auction_end_time");
        if (!rs.wasNull()) product.setAuctionEndTime(auctionEndTime.toLocalDateTime());

        product.setAvailabilityStatus(Product.AvailabilityStatus.valueOf(rs.getString("availability_status")));
        product.setActive(rs.getBoolean("is_active"));

        return product;
    }
}