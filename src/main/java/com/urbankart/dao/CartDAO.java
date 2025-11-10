package com.urbankart.dao;

import com.urbankart.model.CartItem;
import java.util.List;

public interface CartDAO {
    List<CartItem> getCartItemsByUser(int userId);
    boolean addToCart(int userId, int productId, int quantity);
    boolean updateCartItemQuantity(int cartItemId, int quantity);
    boolean removeFromCart(int cartItemId);
    boolean clearCart(int userId);  // This was missing!
    CartItem getCartItem(int userId, int productId);
}