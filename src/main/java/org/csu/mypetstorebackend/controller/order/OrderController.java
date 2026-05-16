package org.csu.mypetstorebackend.controller.order;

import org.csu.mypetstorebackend.common.ApiResponse;
import org.csu.mypetstorebackend.common.PageResponse;
import org.csu.mypetstorebackend.dto.OrderRequest;
import org.csu.mypetstorebackend.entity.Item;
import org.csu.mypetstorebackend.entity.Orders;
import org.csu.mypetstorebackend.entity.LineItem;
import org.csu.mypetstorebackend.entity.Product;
import org.csu.mypetstorebackend.entity.CartItem;
import org.csu.mypetstorebackend.service.CartService;
import org.csu.mypetstorebackend.service.CatalogService;
import org.csu.mypetstorebackend.service.OrderService;
import org.csu.mypetstorebackend.utils.JwtUtil;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/orders")
public class OrderController {
    private final OrderService orderService;
    private final CatalogService catalogService;
    private final CartService cartService;

    public OrderController(OrderService orderService, CatalogService catalogService, CartService cartService) {
        this.orderService = orderService;
        this.catalogService = catalogService;
        this.cartService = cartService;
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

    private Map<String, Object> buildOrderSummary(Orders order) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("orderId", order.getOrderId());
        response.put("userId", order.getUserId());
        response.put("orderDate", order.getOrderDate());
        response.put("totalPrice", order.getTotalPrice());
        response.put("status", convertOrderStatusToString(order.getOrderStatus()));
        response.put("billToFirstName", order.getBillToFirstName());
        response.put("billToLastName", order.getBillToLastName());
        response.put("courier", order.getCourier());
        response.put("createTime", order.getCreateTime());
        return response;
    }

    private Map<String, Object> buildLineItemResponse(LineItem lineItem) {
        Item item = catalogService.getItemById(lineItem.getItemId());
        Product product = item != null ? catalogService.getProductById(item.getProductId()) : null;
        BigDecimal unitPrice = lineItem.getUnitPrice() != null ? lineItem.getUnitPrice() : BigDecimal.ZERO;
        BigDecimal subtotal = unitPrice.multiply(BigDecimal.valueOf(lineItem.getQuantity()));

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("lineNumber", lineItem.getLineNumber());
        response.put("itemId", lineItem.getItemId());
        response.put("productName", product != null ? product.getName() : null);
        response.put("quantity", lineItem.getQuantity());
        response.put("unitPrice", unitPrice);
        response.put("subtotal", subtotal);
        return response;
    }

    private List<Map<String, Object>> buildLineItemResponses(List<LineItem> lineItems) {
        List<Map<String, Object>> response = new ArrayList<>();
        for (LineItem lineItem : lineItems) {
            response.add(buildLineItemResponse(lineItem));
        }
        return response;
    }

    @PostMapping
    public ApiResponse<Object> createOrder(
            @RequestHeader(value = "Authorization", required = false) String token,
            @RequestBody OrderRequest request) {
        String username = extractUsernameFromToken(token);
        if (username == null) {
            return ApiResponse.unauthorized("Authentication required. Please sign in first.");
        }
        List<CartItem> cartItems = cartService.getCartItems(username);
        if (cartItems.isEmpty()) {
            return ApiResponse.badRequest("Cannot create an order from an empty cart");
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
        order.setExpiryDate("12/30");
        order.setCourier("UPS");
        order.setLocale("zh_CN");
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
        response.put("lineItems", buildLineItemResponses(lineItems));
        response.put("createTime", created.getCreateTime());

        return ApiResponse.created("Order created successfully", response);
    }

    @GetMapping
    public ApiResponse<PageResponse<Map<String, Object>>> getMyOrders(
            @RequestHeader(value = "Authorization", required = false) String token,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String status) {
        String username = extractUsernameFromToken(token);
        if (username == null) {
            return ApiResponse.unauthorized("Authentication required. Please sign in first.");
        }

        PageResponse<Orders> result = orderService.getOrdersByUserId(username, page, pageSize, status);
        List<Map<String, Object>> items = new ArrayList<>();
        for (Orders order : result.getItems()) {
            items.add(buildOrderSummary(order));
        }
        return ApiResponse.success(new PageResponse<>(result.getTotal(), result.getPage(), result.getPageSize(), items));
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
        if (!username.equals(order.getUserId())) {
            return ApiResponse.forbidden("You do not have permission to view this order");
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
        response.put("lineItems", buildLineItemResponses(lineItems));
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
        if (!username.equals(order.getUserId())) {
            return ApiResponse.forbidden("You do not have permission to cancel this order");
        }

        // Check if order status is pending (only pending orders can be cancelled)
        if (order.getOrderStatus() != 0) {
            return ApiResponse.badRequest("Only pending orders can be cancelled");
        }

        orderService.cancelOrder(orderId);
        return ApiResponse.success("Order cancelled successfully", null);
    }
}
