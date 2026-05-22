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
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

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
        queryWrapper.eq("orderid", orderId).orderByDesc("timestamp").last("LIMIT 1");
        OrderStatus status = orderStatusMapper.selectOne(queryWrapper);
        if (status == null) {
            return 0;
        }
        String statusStr = status.getStatus();
        if (statusStr == null) {
            return 0;
        }
        switch (statusStr) {
            case "P":
            case "pending":
                return 0;
            case "S":
            case "shipped":
                return 1;
            case "D":
            case "delivered":
                return 2;
            case "C":
            case "cancelled":
                return 3;
            default:
                return 0;
        }
    }

    private void saveOrderStatus(int orderId, int status) {
        QueryWrapper<OrderStatus> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("orderid", orderId).eq("linenum", 0).last("LIMIT 1");
        OrderStatus orderStatus = orderStatusMapper.selectOne(queryWrapper);
        boolean isNewStatus = orderStatus == null;

        if (isNewStatus) {
            orderStatus = new OrderStatus();
            orderStatus.setOrderId(orderId);
            orderStatus.setLineNumber(0);
        }

        orderStatus.setTimestamp(new java.util.Date());
        String statusStr;
        switch (status) {
            case 0:
                statusStr = "P";
                break;
            case 1:
                statusStr = "S";
                break;
            case 2:
                statusStr = "D";
                break;
            case 3:
                statusStr = "C";
                break;
            default:
                statusStr = "P";
                break;
        }
        orderStatus.setStatus(statusStr);

        if (isNewStatus) {
            orderStatusMapper.insert(orderStatus);
        } else {
            QueryWrapper<OrderStatus> updateWrapper = new QueryWrapper<>();
            updateWrapper.eq("orderid", orderId).eq("linenum", 0);
            orderStatusMapper.update(orderStatus, updateWrapper);
        }
    }

    private void attachLatestStatus(Orders order) {
        if (order != null) {
            order.setOrderStatus(getLatestOrderStatus(order.getOrderId()));
        }
    }

    @Override
    @Transactional
    public Orders createOrder(String userId, Orders order) {
        order.setUserId(userId);
        order.setOrderDate(LocalDate.now().format(DateTimeFormatter.ISO_DATE));
        order.setOrderId(new Random().nextInt(Integer.MAX_VALUE));
        order.setTotalPrice(BigDecimal.ZERO);

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
            lineItemMapper.insert(lineItem);
        }

        saveOrderStatus(order.getOrderId(), 0);
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
            records = records.stream().filter(order -> order.getOrderStatus() == statusValue).collect(Collectors.toList());
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
            records = records.stream().filter(order -> order.getOrderStatus() == statusValue).collect(Collectors.toList());
        }
        return new PageResponse<>(statusValue >= 0 ? records.size() : result.getTotal(), page, pageSize, records);
    }

    @Override
    @Transactional
    public Orders updateOrderStatus(int orderId, String status) {
        Orders order = getOrderById(orderId);
        if (order != null) {
            int statusValue = convertStatusToInt(status);
            if (statusValue >= 0) {
                saveOrderStatus(orderId, statusValue);
                order.setOrderStatus(statusValue);
            }
            QueryWrapper<Orders> updateWrapper = new QueryWrapper<>();
            updateWrapper.eq("orderid", orderId);
            ordersMapper.update(order, updateWrapper);
        }
        return order;
    }

    @Override
    @Transactional
    public void cancelOrder(int orderId) {
        Orders order = getOrderById(orderId);
        if (order != null) {
            saveOrderStatus(orderId, 3);
            order.setOrderStatus(3);
            QueryWrapper<Orders> updateWrapper = new QueryWrapper<>();
            updateWrapper.eq("orderid", orderId);
            ordersMapper.update(order, updateWrapper);
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
            saveOrderStatus(orderId, 1);
            order.setOrderStatus(1);
            QueryWrapper<Orders> updateWrapper = new QueryWrapper<>();
            updateWrapper.eq("orderid", orderId);
            ordersMapper.update(order, updateWrapper);
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
