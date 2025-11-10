package com.urbankart.dao;

import com.urbankart.model.OrderItem;
import java.util.List;

public interface OrderItemDAO {
    List<OrderItem> getOrderItemsByOrder(int orderId);
    boolean createOrderItem(OrderItem orderItem);
}