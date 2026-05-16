package org.csu.mypetstorebackend.controller.order;

import org.csu.mypetstorebackend.common.ApiResponse;
import org.csu.mypetstorebackend.common.PageResponse;
import org.csu.mypetstorebackend.dto.OrderRequest;
import org.csu.mypetstorebackend.entity.Orders;
import org.csu.mypetstorebackend.entity.LineItem;
import org.csu.mypetstorebackend.service.OrderService;
import org.csu.mypetstorebackend.utils.JwtUtil;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    private String extractUsernameFromToken(String token) {
        if (token == null || !token.startsWith("Bearer ")) {
            return null;
        }
        String actualToken = token.substring(7);
        if (!JwtUtil.validateToken(actualToken)) {
            return null;
        }
        return JwtUtil.extractUsername(actualToken);
    }

    @PostMapping
    public ApiResponse<Object> createOrder(
            @RequestHeader(value = "Authorization", required = false) String token,
            @RequestBody OrderRequest request) {
        String username = extractUsernameFromToken(token);
        if (username == null) {
            return ApiResponse.unauthorized("Authentication required. Please sign in first.");
        }

        Orders order = new Orders();
        order.setBillToFirstName(request.getBillToFirstName());
        order.setBillToLastName(request.getBillToLastName());
        order.setBillAddress1(request.getBillAddress1());
        order.setBillAddress2(request.getBillAddress2());
        order.setBillCity(request.getBillCity());
        order.setBillState(request.getBillState());
        order.setBillZip(request.getBillZip());
        order.setBillCountry(request.getBillCountry());
        order.setShipToFirstName(request.getShipToFirstName());
        order.setShipToLastName(request.getShipToLastName());
        order.setShipAddress1(request.getShipAddress1());
        order.setShipAddress2(request.getShipAddress2());
        order.setShipCity(request.getShipCity());
        order.setShipState(request.getShipState());
        order.setShipZip(request.getShipZip());
        order.setShipCountry(request.getShipCountry());
        order.setCreditCard(request.getCreditCard());
        order.setCardType(request.getCardType());
        order.setOrderStatus(0); // pending

        Orders created = orderService.createOrder(username, order);
        List<LineItem> lineItems = orderService.getOrderLineItems(created.getOrderId());

        Object response = new Object() {
            public int orderId = created.getOrderId();
            public String userId = created.getUserId();
            public String orderDate = created.getOrderDate();
            public Object totalPrice = created.getTotalPrice();
            public String billToFirstName = created.getBillToFirstName();
            public String billToLastName = created.getBillToLastName();
            public String shipToFirstName = created.getShipToFirstName();
            public String shipToLastName = created.getShipToLastName();
            public String cardType = created.getCardType();
            public String status = "pending";
            public List<LineItem> lineItems = lineItems;
            public String createTime = created.getCreateTime();
        };

        return ApiResponse.created("Order created successfully", response);
    }

    @GetMapping
    public ApiResponse<PageResponse<Orders>> getMyOrders(
            @RequestHeader(value = "Authorization", required = false) String token,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String status) {
        String username = extractUsernameFromToken(token);
        if (username == null) {
            return ApiResponse.unauthorized("Authentication required. Please sign in first.");
        }

        PageResponse<Orders> result = orderService.getOrdersByUserId(username, page, pageSize, status);
        return ApiResponse.success(result);
    }

    @GetMapping("/{orderId}")
    public ApiResponse<Object> getOrderDetail(
            @PathVariable int orderId,
            @RequestHeader(value = "Authorization", required = false) String token) {
        String username = extractUsernameFromToken(token);
        if (username == null) {
            return ApiResponse.unauthorized("Authentication required. Please sign in first.");
        }

        Orders order = orderService.getOrderById(orderId);
        if (order == null) {
            return ApiResponse.notFound("Order not found");
        }

        List<LineItem> lineItems = orderService.getOrderLineItems(orderId);

        Object response = new Object() {
            public int orderId = order.getOrderId();
            public String userId = order.getUserId();
            public String orderDate = order.getOrderDate();
            public Object totalPrice = order.getTotalPrice();
            public String status = "pending";
            public String billToFirstName = order.getBillToFirstName();
            public String billToLastName = order.getBillToLastName();
            public String billAddress1 = order.getBillAddress1();
            public String billAddress2 = order.getBillAddress2();
            public String billCity = order.getBillCity();
            public String billState = order.getBillState();
            public String billZip = order.getBillZip();
            public String billCountry = order.getBillCountry();
            public String shipToFirstName = order.getShipToFirstName();
            public String shipToLastName = order.getShipToLastName();
            public String shipAddress1 = order.getShipAddress1();
            public String shipAddress2 = order.getShipAddress2();
            public String shipCity = order.getShipCity();
            public String shipState = order.getShipState();
            public String shipZip = order.getShipZip();
            public String shipCountry = order.getShipCountry();
            public String courier = order.getCourier();
            public String cardType = order.getCardType();
            public List<LineItem> lineItems = lineItems;
            public String createTime = order.getCreateTime();
        };

        return ApiResponse.success(response);
    }

    @DeleteMapping("/{orderId}")
    public ApiResponse<Object> cancelOrder(
            @PathVariable int orderId,
            @RequestHeader(value = "Authorization", required = false) String token) {
        String username = extractUsernameFromToken(token);
        if (username == null) {
            return ApiResponse.unauthorized("Authentication required. Please sign in first.");
        }

        Orders order = orderService.getOrderById(orderId);
        if (order == null) {
            return ApiResponse.notFound("Order not found");
        }

        // Check if order status is pending (only pending orders can be cancelled)
        if (order.getOrderStatus() != 0) {
            return ApiResponse.badRequest("Only pending orders can be cancelled");
        }

        orderService.cancelOrder(orderId);
        return ApiResponse.success("Order cancelled successfully", null);
    }
}

