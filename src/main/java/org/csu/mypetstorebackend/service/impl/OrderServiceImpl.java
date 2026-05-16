package org.csu.mypetstorebackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.csu.mypetstorebackend.common.PageResponse;
import org.csu.mypetstorebackend.entity.Orders;
import org.csu.mypetstorebackend.entity.LineItem;
import org.csu.mypetstorebackend.entity.CartItem;
import org.csu.mypetstorebackend.persistence.OrdersMapper;
import org.csu.mypetstorebackend.persistence.LineItemMapper;
import org.csu.mypetstorebackend.persistence.CartItemMapper;
import org.csu.mypetstorebackend.persistence.CartMapper;
import org.csu.mypetstorebackend.service.OrderService;
import org.csu.mypetstorebackend.service.CartService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Random;

@Service("orderService")
public class OrderServiceImpl implements OrderService {
    private final OrdersMapper ordersMapper;
    private final LineItemMapper lineItemMapper;
    private final CartItemMapper cartItemMapper;
    private final CartMapper cartMapper;
    private final CartService cartService;

    public OrderServiceImpl(OrdersMapper ordersMapper, LineItemMapper lineItemMapper,
                          CartItemMapper cartItemMapper, CartMapper cartMapper, CartService cartService) {
        this.ordersMapper = ordersMapper;
        this.lineItemMapper = lineItemMapper;
        this.cartItemMapper = cartItemMapper;
        this.cartMapper = cartMapper;
        this.cartService = cartService;
    }

    private String getCurrentTimestamp() {
        return LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME) + "Z";
    }

    @Override
    public Orders createOrder(String userId, Orders order) {
        order.setUserId(userId);
        order.setOrderDate(LocalDate.now().format(DateTimeFormatter.ISO_DATE));
        order.setOrderId(new Random().nextInt(Integer.MAX_VALUE));
        order.setCreateTime(getCurrentTimestamp());
        order.setUpdateTime(getCurrentTimestamp());

        ordersMapper.insert(order);

        // Create line items from cart
        List<CartItem> cartItems = cartService.getCartItems(userId);
        int lineNumber = 1;
        for (CartItem cartItem : cartItems) {
            LineItem lineItem = new LineItem();
            lineItem.setOrderId(order.getOrderId());
            lineItem.setLineNumber(lineNumber++);
            lineItem.setItemId(cartItem.getItemId());
            lineItem.setQuantity(cartItem.getQuantity());
            // Note: unitPrice should be fetched from Item entity in production
            lineItem.setCreateTime(getCurrentTimestamp());
            lineItem.setUpdateTime(getCurrentTimestamp());
            lineItemMapper.insert(lineItem);
        }

        // Clear the cart after order creation
        cartService.clearCart(userId);

        return order;
    }

    @Override
    public Orders getOrderById(int orderId) {
        QueryWrapper<Orders> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("orderid", orderId);
        Orders order = ordersMapper.selectOne(queryWrapper);

        if (order != null) {
            List<LineItem> lineItems = getOrderLineItems(orderId);
            order.setLineItems(lineItems);
        }

        return order;
    }

    @Override
    public PageResponse<Orders> getOrdersByUserId(String userId, int page, int pageSize, String status) {
        Page<Orders> pageObj = new Page<>(page, pageSize);
        QueryWrapper<Orders> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userid", userId);
        if (status != null && !status.isEmpty()) {
            queryWrapper.eq("orderStatus", status);
        }
        Page<Orders> result = ordersMapper.selectPage(pageObj, queryWrapper);
        return new PageResponse<>(result.getTotal(), page, pageSize, result.getRecords());
    }

    @Override
    public PageResponse<Orders> getAllOrders(int page, int pageSize, String status, String userId) {
        Page<Orders> pageObj = new Page<>(page, pageSize);
        QueryWrapper<Orders> queryWrapper = new QueryWrapper<>();
        if (status != null && !status.isEmpty()) {
            queryWrapper.eq("orderStatus", status);
        }
        if (userId != null && !userId.isEmpty()) {
            queryWrapper.eq("userid", userId);
        }
        Page<Orders> result = ordersMapper.selectPage(pageObj, queryWrapper);
        return new PageResponse<>(result.getTotal(), page, pageSize, result.getRecords());
    }

    @Override
    public Orders updateOrderStatus(int orderId, String status) {
        Orders order = getOrderById(orderId);
        if (order != null) {
            order.setUpdateTime(getCurrentTimestamp());
            ordersMapper.updateById(order);
        }
        return order;
    }

    @Override
    public void cancelOrder(int orderId) {
        Orders order = getOrderById(orderId);
        if (order != null) {
            order.setUpdateTime(getCurrentTimestamp());
            ordersMapper.updateById(order);
        }
    }

    @Override
    public void deleteOrder(int orderId) {
        QueryWrapper<Orders> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("orderid", orderId);
        ordersMapper.delete(queryWrapper);

        // Delete associated line items
        QueryWrapper<LineItem> lineItemQuery = new QueryWrapper<>();
        lineItemQuery.eq("orderid", orderId);
        lineItemMapper.delete(lineItemQuery);
    }

    @Override
    public void shipOrder(int orderId) {
        Orders order = getOrderById(orderId);
        if (order != null) {
            order.setUpdateTime(getCurrentTimestamp());
            ordersMapper.updateById(order);
        }
    }

    @Override
    public PageResponse<Orders> searchOrders(String keyword, int page, int pageSize) {
        Page<Orders> pageObj = new Page<>(page, pageSize);
        QueryWrapper<Orders> queryWrapper = new QueryWrapper<>();
        try {
            int orderId = Integer.parseInt(keyword);
            queryWrapper.eq("orderid", orderId);
        } catch (NumberFormatException e) {
            queryWrapper.like("userid", keyword);
        }
        Page<Orders> result = ordersMapper.selectPage(pageObj, queryWrapper);
        return new PageResponse<>(result.getTotal(), page, pageSize, result.getRecords());
    }

    @Override
    public List<LineItem> getOrderLineItems(int orderId) {
        QueryWrapper<LineItem> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("orderid", orderId);
        return lineItemMapper.selectList(queryWrapper);
    }
}

