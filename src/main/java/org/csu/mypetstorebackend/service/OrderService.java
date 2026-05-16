package org.csu.mypetstorebackend.service;

import org.csu.mypetstorebackend.entity.Orders;
import org.csu.mypetstorebackend.entity.LineItem;
import org.csu.mypetstorebackend.common.PageResponse;

import java.util.List;

public interface OrderService {
    Orders createOrder(String userId, Orders order);
    Orders getOrderById(int orderId);
    PageResponse<Orders> getOrdersByUserId(String userId, int page, int pageSize, String status);
    PageResponse<Orders> getAllOrders(int page, int pageSize, String status, String userId);
    Orders updateOrderStatus(int orderId, String status);
    void cancelOrder(int orderId);
    void deleteOrder(int orderId);
    void shipOrder(int orderId);
    PageResponse<Orders> searchOrders(String keyword, int page, int pageSize);
    List<LineItem> getOrderLineItems(int orderId);
}

