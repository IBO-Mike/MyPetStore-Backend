package org.csu.mypetstorebackend.controller.cart;

import org.csu.mypetstorebackend.common.ApiResponse;
import org.csu.mypetstorebackend.dto.CartItemRequest;
import org.csu.mypetstorebackend.entity.Cart;
import org.csu.mypetstorebackend.entity.CartItem;
import org.csu.mypetstorebackend.entity.Item;
import org.csu.mypetstorebackend.entity.Product;
import org.csu.mypetstorebackend.persistence.ItemMapper;
import org.csu.mypetstorebackend.persistence.ProductMapper;
import org.csu.mypetstorebackend.service.CartService;
import org.csu.mypetstorebackend.utils.JwtUtil;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/cart")
public class CartController {
    private final CartService cartService;
    private final ItemMapper itemMapper;
    private final ProductMapper productMapper;

    public CartController(CartService cartService, ItemMapper itemMapper, ProductMapper productMapper) {
        this.cartService = cartService;
        this.itemMapper = itemMapper;
        this.productMapper = productMapper;
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

    @GetMapping
    public ApiResponse<Object> getCart(@RequestHeader(value = "Authorization", required = false) String token) {
        String username = extractUsernameFromToken(token);
        if (username == null) {
            return ApiResponse.unauthorized("Authentication required. Please sign in first.");
        }

        Cart cart = cartService.getCartByUserId(username);
        List<CartItem> cartItems = cartService.getCartItems(username);

        BigDecimal totalPrice = BigDecimal.ZERO;
        List<Object> items = new ArrayList<>();

        for (CartItem cartItem : cartItems) {
            Item item = itemMapper.selectById(cartItem.getItemId());
            if (item != null) {
                BigDecimal subtotal = item.getListPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity()));
                totalPrice = totalPrice.add(subtotal);

                // Get product name
                String productName = "Unknown Product";
                Product product = productMapper.selectById(item.getProductId());
                if (product != null) {
                    productName = product.getName();
                }

                Map<String, Object> itemObj = new LinkedHashMap<>();
                itemObj.put("itemId", cartItem.getItemId());
                itemObj.put("productId", item.getProductId());
                itemObj.put("productName", productName);
                itemObj.put("quantity", cartItem.getQuantity());
                itemObj.put("listPrice", item.getListPrice());
                itemObj.put("subtotal", subtotal);
                itemObj.put("attribute1", item.getAttribute1());
                items.add(itemObj);
            }
        }

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("cartId", cart.getCartId());
        response.put("userId", cart.getUserId());
        response.put("items", items);
        response.put("totalItems", cartItems.size());
        response.put("totalPrice", totalPrice);
        response.put("createTime", cart.getCreateTime());
        response.put("updateTime", cart.getUpdateTime());

        return ApiResponse.success(response);
    }

    @PostMapping("/items")
    public ApiResponse<Object> addItemToCart(
            @RequestHeader(value = "Authorization", required = false) String token,
            @RequestBody CartItemRequest request) {
        String username = extractUsernameFromToken(token);
        if (username == null) {
            return ApiResponse.unauthorized("Authentication required. Please sign in first.");
        }

        if (request.getItemId() == null || request.getQuantity() <= 0) {
            return ApiResponse.badRequest("Invalid item ID or quantity");
        }

        cartService.addItemToCart(username, request.getItemId(), request.getQuantity());

        Item item = itemMapper.selectById(request.getItemId());
        BigDecimal subtotal = item != null ? item.getListPrice().multiply(BigDecimal.valueOf(request.getQuantity())) : BigDecimal.ZERO;

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("cartId", 1); // Should get from cart
        response.put("itemId", request.getItemId());
        response.put("quantity", request.getQuantity());
        response.put("listPrice", item != null ? item.getListPrice() : BigDecimal.ZERO);
        response.put("subtotal", subtotal);

        return ApiResponse.created("Item added to cart successfully", response);
    }

    @PutMapping("/items/{itemId}")
    public ApiResponse<Object> updateItemQuantity(
            @PathVariable String itemId,
            @RequestHeader(value = "Authorization", required = false) String token,
            @RequestBody CartItemRequest request) {
        String username = extractUsernameFromToken(token);
        if (username == null) {
            return ApiResponse.unauthorized("Authentication required. Please sign in first.");
        }

        if (request.getQuantity() <= 0) {
            return ApiResponse.badRequest("Invalid quantity");
        }

        cartService.updateCartItemQuantity(username, itemId, request.getQuantity());

        Item item = itemMapper.selectById(itemId);
        BigDecimal subtotal = item != null ? item.getListPrice().multiply(BigDecimal.valueOf(request.getQuantity())) : BigDecimal.ZERO;

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("updatedItemId", itemId);
        response.put("quantity", request.getQuantity());
        response.put("listPrice", item != null ? item.getListPrice() : BigDecimal.ZERO);
        response.put("subtotal", subtotal);

        return ApiResponse.success("Cart item updated successfully", response);
    }

    @DeleteMapping("/items/{itemId}")
    public ApiResponse<Void> removeItemFromCart(
            @PathVariable String itemId,
            @RequestHeader(value = "Authorization", required = false) String token) {
        String username = extractUsernameFromToken(token);
        if (username == null) {
            return ApiResponse.unauthorized("Authentication required. Please sign in first.");
        }

        cartService.removeItemFromCart(username, itemId);
        return ApiResponse.success("Item removed from cart", null);
    }

    @DeleteMapping
    public ApiResponse<Object> clearCart(@RequestHeader(value = "Authorization", required = false) String token) {
        String username = extractUsernameFromToken(token);
        if (username == null) {
            return ApiResponse.unauthorized("Authentication required. Please sign in first.");
        }

        cartService.clearCart(username);
        return ApiResponse.success("Cart cleared successfully", null);
    }
}
