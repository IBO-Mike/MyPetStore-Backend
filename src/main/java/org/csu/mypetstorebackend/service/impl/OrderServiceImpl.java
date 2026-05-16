package org.csu.mypetstorebackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.csu.mypetstorebackend.common.PageResponse;
import org.csu.mypetstorebackend.entity.Orders;
import org.csu.mypetstorebackend.entity.LineItem;
import org.csu.mypetstorebackend.entity.CartItem;
import org.csu.mypetstorebackend.entity.Item;
import org.csu.mypetstorebackend.entity.OrderStatus;
import org.csu.mypetstorebackend.persistence.OrdersMapper;
import org.csu.mypetstorebackend.persistence.LineItemMapper;
import org.csu.mypetstorebackend.persistence.CartItemMapper;
import org.csu.mypetstorebackend.persistence.CartMapper;
import org.csu.mypetstorebackend.persistence.ItemMapper;
import org.csu.mypetstorebackend.persistence.OrderStatusMapper;
import org.csu.mypetstorebackend.service.OrderService;
import org.csu.mypetstorebackend.service.CartService;
import org.csu.mypetstorebackend.utils.TimeUtil;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Random;

@Service("orderService")
public class OrderServiceImpl implements OrderService {
    private final OrdersMapper ordersMapper;
    private final LineItemMapper lineItemMapper;
    private final CartItemMapper cartItemMapper;
    private final CartMapper cartMapper;
    private final ItemMapper itemMapper;
    private final OrderStatusMapper orderStatusMapper;
    private final CartService cartService;

    public OrderServiceImpl(OrdersMapper ordersMapper, LineItemMapper lineItemMapper,
                          CartItemMapper cartItemMapper, CartMapper cartMapper,
                          ItemMapper itemMapper, OrderStatusMapper orderStatusMapper,
                          CartService cartService) {
        this.ordersMapper = ordersMapper;
        this.lineItemMapper = lineItemMapper;
        this.cartItemMapper = cartItemMapper;
        this.cartMapper = cartMapper;
        this.itemMapper = itemMapper;
        this.orderStatusMapper = orderStatusMapper;
        this.cartService = cartService;
    }

    private String getCurrentTimestamp() {
        return TimeUtil.currentMysqlDateTime();
    }

    private int convertStatusToInt(String status) {
        if (status == null || status.isEmpty()) {
            return -1;
        }
        switch (status.toLowerCase()) {
            case "pending":
                return 0;
            case "shipped":
                return 1;
            case "delivered":
                return 2;
            case "cancelled":
                return 3;
            default:
                return -1;
        }
    }

    private Item getItemByItemId(String itemId) {
        QueryWrapper<Item> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("itemid", itemId);
        return itemMapper.selectOne(queryWrapper);
    }

    private int getLatestOrderStatus(int orderId) {
        QueryWrapper<OrderStatus> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("orderid", orderId).orderByDesc("timestamp").orderByDesc("id").last("LIMIT 1");
        OrderStatus status = orderStatusMapper.selectOne(queryWrapper);
        return status == null ? 0 : status.getStatus();
    }

    private void appendOrderStatus(int orderId, int status) {
        OrderStatus orderStatus = new OrderStatus();
        orderStatus.setOrderId(orderId);
        orderStatus.setLineNumber(0);
        orderStatus.setTimestamp(new java.util.Date());
        orderStatus.setStatus(status);
        orderStatus.setCreateTime(getCurrentTimestamp());
        orderStatus.setUpdateTime(getCurrentTimestamp());
        orderStatusMapper.insert(orderStatus);
    }

    private void attachLatestStatus(Orders order) {
        if (order != null) {
            order.setOrderStatus(getLatestOrderStatus(order.getOrderId()));
        }
    }

    @Override
    public Orders createOrder(String userId, Orders order) {
        order.setUserId(userId);
        order.setOrderDate(LocalDate.now().format(DateTimeFormatter.ISO_DATE));
        order.setOrderId(new Random().nextInt(Integer.MAX_VALUE));
        order.setTotalPrice(BigDecimal.ZERO);
        order.setCreateTime(getCurrentTimestamp());
        order.setUpdateTime(getCurrentTimestamp());

        List<CartItem> cartItems = cartService.getCartItems(userId);
        BigDecimal totalPrice = BigDecimal.ZERO;
        for (CartItem cartItem : cartItems) {
            Item item = getItemByItemId(cartItem.getItemId());
            if (item != null && item.getListPrice() != null) {
                totalPrice = totalPrice.add(item.getListPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity())));
            }
        }
        order.setTotalPrice(totalPrice);

        ordersMapper.insert(order);

        // Create line items from cart
        int lineNumber = 1;
        for (CartItem cartItem : cartItems) {
            Item item = getItemByItemId(cartItem.getItemId());
            LineItem lineItem = new LineItem();
            lineItem.setOrderId(order.getOrderId());
            lineItem.setLineNumber(lineNumber++);
            lineItem.setItemId(cartItem.getItemId());
            lineItem.setQuantity(cartItem.getQuantity());
            lineItem.setUnitPrice(item != null ? item.getListPrice() : BigDecimal.ZERO);
            lineItem.setCreateTime(getCurrentTimestamp());
            lineItem.setUpdateTime(getCurrentTimestamp());
            lineItemMapper.insert(lineItem);
        }

        appendOrderStatus(order.getOrderId(), 0);
        order.setOrderStatus(0);

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
            attachLatestStatus(order);
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
        Page<Orders> result = ordersMapper.selectPage(pageObj, queryWrapper);
        List<Orders> records = result.getRecords();
        records.forEach(this::attachLatestStatus);
        int statusValue = convertStatusToInt(status);
        if (statusValue >= 0) {
            records = records.stream().filter(order -> order.getOrderStatus() == statusValue).toList();
        }
        return new PageResponse<>(statusValue >= 0 ? records.size() : result.getTotal(), page, pageSize, records);
    }

    @Override
    public PageResponse<Orders> getAllOrders(int page, int pageSize, String status, String userId) {
        Page<Orders> pageObj = new Page<>(page, pageSize);
        QueryWrapper<Orders> queryWrapper = new QueryWrapper<>();
        if (userId != null && !userId.isEmpty()) {
            queryWrapper.eq("userid", userId);
        }
        Page<Orders> result = ordersMapper.selectPage(pageObj, queryWrapper);
        List<Orders> records = result.getRecords();
        records.forEach(this::attachLatestStatus);
        int statusValue = convertStatusToInt(status);
        if (statusValue >= 0) {
            records = records.stream().filter(order -> order.getOrderStatus() == statusValue).toList();
        }
        return new PageResponse<>(statusValue >= 0 ? records.size() : result.getTotal(), page, pageSize, records);
    }

    @Override
    public Orders updateOrderStatus(int orderId, String status) {
        Orders order = getOrderById(orderId);
        if (order != null) {
            int statusValue = convertStatusToInt(status);
            if (statusValue >= 0) {
                appendOrderStatus(orderId, statusValue);
                order.setOrderStatus(statusValue);
            }
            order.setUpdateTime(getCurrentTimestamp());
            ordersMapper.updateById(order);
        }
        return order;
    }

    @Override
    public void cancelOrder(int orderId) {
        Orders order = getOrderById(orderId);
        if (order != null) {
            appendOrderStatus(orderId, 3);
            order.setOrderStatus(3);
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
            appendOrderStatus(orderId, 1);
            order.setOrderStatus(1);
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
        List<Orders> records = result.getRecords();
        records.forEach(this::attachLatestStatus);
        return new PageResponse<>(result.getTotal(), page, pageSize, records);
    }

    @Override
    public List<LineItem> getOrderLineItems(int orderId) {
        QueryWrapper<LineItem> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("orderid", orderId);
        return lineItemMapper.selectList(queryWrapper);
    }
}
