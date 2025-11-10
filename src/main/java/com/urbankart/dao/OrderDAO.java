package com.urbankart.dao;

import com.urbankart.model.Order;
import java.util.List;

public interface OrderDAO {
    Order getOrderById(int id);
    List<Order> getOrdersByUser(int userId);
    boolean createOrder(Order order);
    boolean updateOrderStatus(int orderId, Order.OrderStatus status);
}