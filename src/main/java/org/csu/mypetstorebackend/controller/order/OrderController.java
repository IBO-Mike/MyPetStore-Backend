package org.csu.mypetstorebackend.controller.order;

import org.csu.mypetstorebackend.common.ApiResponse;
import org.csu.mypetstorebackend.common.PageResponse;
import org.csu.mypetstorebackend.dto.OrderRequest;
import org.csu.mypetstorebackend.entity.Orders;
import org.csu.mypetstorebackend.entity.LineItem;
import org.csu.mypetstorebackend.service.OrderService;
import org.csu.mypetstorebackend.utils.JwtUtil;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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

    private String convertOrderStatusToString(int status) {
        switch (status) {
            case 0:
                return "pending";
            case 1:
                return "shipped";
            case 2:
                return "delivered";
            case 3:
                return "cancelled";
            default:
                return "unknown";
        }
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

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("orderId", created.getOrderId());
        response.put("userId", created.getUserId());
        response.put("orderDate", created.getOrderDate());
        response.put("totalPrice", created.getTotalPrice());
        response.put("billToFirstName", created.getBillToFirstName());
        response.put("billToLastName", created.getBillToLastName());
        response.put("shipToFirstName", created.getShipToFirstName());
        response.put("shipToLastName", created.getShipToLastName());
        response.put("cardType", created.getCardType());
        response.put("status", convertOrderStatusToString(created.getOrderStatus()));
        response.put("lineItems", lineItems);
        response.put("createTime", created.getCreateTime());

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

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("orderId", order.getOrderId());
        response.put("userId", order.getUserId());
        response.put("orderDate", order.getOrderDate());
        response.put("totalPrice", order.getTotalPrice());
        response.put("status", convertOrderStatusToString(order.getOrderStatus()));
        response.put("billToFirstName", order.getBillToFirstName());
        response.put("billToLastName", order.getBillToLastName());
        response.put("billAddress1", order.getBillAddress1());
        response.put("billAddress2", order.getBillAddress2());
        response.put("billCity", order.getBillCity());
        response.put("billState", order.getBillState());
        response.put("billZip", order.getBillZip());
        response.put("billCountry", order.getBillCountry());
        response.put("shipToFirstName", order.getShipToFirstName());
        response.put("shipToLastName", order.getShipToLastName());
        response.put("shipAddress1", order.getShipAddress1());
        response.put("shipAddress2", order.getShipAddress2());
        response.put("shipCity", order.getShipCity());
        response.put("shipState", order.getShipState());
        response.put("shipZip", order.getShipZip());
        response.put("shipCountry", order.getShipCountry());
        response.put("courier", order.getCourier());
        response.put("cardType", order.getCardType());
        response.put("lineItems", lineItems);
        response.put("createTime", order.getCreateTime());

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
