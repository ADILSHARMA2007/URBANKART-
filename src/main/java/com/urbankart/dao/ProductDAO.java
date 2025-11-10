package com.urbankart.dao;

import com.urbankart.model.Product;
import java.util.List;

public interface ProductDAO {
    Product getProductById(int id);
    List<Product> getAllProducts();
    List<Product> getProductsBySeller(int sellerId);
    List<Product> getProductsByCategory(String category);
    List<Product> searchProducts(String query);
    List<Product> getProductsByType(Product.ListingType type);
    boolean createProduct(Product product);
    boolean updateProduct(Product product);
    boolean deleteProduct(int id);
    boolean updateProductPrice(int productId, java.math.BigDecimal newPrice);
}