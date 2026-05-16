package org.csu.mypetstorebackend.controller.cart;

import org.csu.mypetstorebackend.common.ApiResponse;
import org.csu.mypetstorebackend.dto.CartItemRequest;
import org.csu.mypetstorebackend.entity.Cart;
import org.csu.mypetstorebackend.entity.CartItem;
import org.csu.mypetstorebackend.entity.Item;
import org.csu.mypetstorebackend.persistence.ItemMapper;
import org.csu.mypetstorebackend.service.CartService;
import org.csu.mypetstorebackend.utils.JwtUtil;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/cart")
public class CartController {
    private final CartService cartService;
    private final ItemMapper itemMapper;

    public CartController(CartService cartService, ItemMapper itemMapper) {
        this.cartService = cartService;
        this.itemMapper = itemMapper;
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

                Object itemObj = new Object() {
                    public String itemId = cartItem.getItemId();
                    public String productId = item.getProductId();
                    public String productName = "Product"; // Should get from Product entity
                    public int quantity = cartItem.getQuantity();
                    public BigDecimal listPrice = item.getListPrice();
                    public BigDecimal subtotal = subtotal;
                    public String attribute1 = item.getAttribute1();
                };
                items.add(itemObj);
            }
        }

        Object response = new Object() {
            public int cartId = cart.getCartId();
            public String userId = cart.getUserId();
            public List<Object> items = items;
            public int totalItems = cartItems.size();
            public BigDecimal totalPrice = totalPrice;
            public String createTime = cart.getCreateTime();
            public String updateTime = cart.getUpdateTime();
        };

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

        Object response = new Object() {
            public int cartId = 1; // Should get from cart
            public String itemId = request.getItemId();
            public int quantity = request.getQuantity();
            public BigDecimal listPrice = item != null ? item.getListPrice() : BigDecimal.ZERO;
            public BigDecimal subtotal = subtotal;
        };

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

        Object response = new Object() {
            public String updatedItemId = itemId;
            public int quantity = request.getQuantity();
            public BigDecimal listPrice = item != null ? item.getListPrice() : BigDecimal.ZERO;
            public BigDecimal subtotal = subtotal;
        };

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
        return new ApiResponse<>(204, "Item removed from cart", null);
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

