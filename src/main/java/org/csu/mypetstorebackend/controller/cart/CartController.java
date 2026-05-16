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
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
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

    private Item getItemByItemId(String itemId) {
        QueryWrapper<Item> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("itemid", itemId);
        return itemMapper.selectOne(queryWrapper);
    }

    private Product getProductByProductId(String productId) {
        QueryWrapper<Product> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("productid", productId);
        return productMapper.selectOne(queryWrapper);
    }

    private CartItem getCartItem(String username, String itemId) {
        return cartService.getCartItems(username).stream()
                .filter(cartItem -> itemId.equals(cartItem.getItemId()))
                .findFirst()
                .orElse(null);
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
        int totalItems = 0;
        List<Object> items = new ArrayList<>();

        for (CartItem cartItem : cartItems) {
            Item item = getItemByItemId(cartItem.getItemId());
            if (item != null) {
                BigDecimal listPrice = item.getListPrice() != null ? item.getListPrice() : BigDecimal.ZERO;
                BigDecimal subtotal = listPrice.multiply(BigDecimal.valueOf(cartItem.getQuantity()));
                totalPrice = totalPrice.add(subtotal);
                totalItems += cartItem.getQuantity();

                // Get product name
                String productName = "Unknown Product";
                Product product = getProductByProductId(item.getProductId());
                if (product != null) {
                    productName = product.getName();
                }

                Map<String, Object> itemObj = new LinkedHashMap<>();
                itemObj.put("itemId", cartItem.getItemId());
                itemObj.put("productId", item.getProductId());
                itemObj.put("productName", productName);
                itemObj.put("quantity", cartItem.getQuantity());
                itemObj.put("listPrice", listPrice);
                itemObj.put("subtotal", subtotal);
                itemObj.put("attribute1", item.getAttribute1());
                items.add(itemObj);
            }
        }

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("cartId", cart.getCartId());
        response.put("userId", cart.getUserId());
        response.put("items", items);
        response.put("totalItems", totalItems);
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

        Item item = getItemByItemId(request.getItemId());
        if (item == null) {
            return ApiResponse.notFound("Item not found with id: " + request.getItemId());
        }

        cartService.addItemToCart(username, request.getItemId(), request.getQuantity());
        Cart cart = cartService.getCartByUserId(username);
        CartItem cartItem = getCartItem(username, request.getItemId());

        BigDecimal listPrice = item.getListPrice() != null ? item.getListPrice() : BigDecimal.ZERO;
        int quantity = cartItem != null ? cartItem.getQuantity() : request.getQuantity();
        BigDecimal subtotal = listPrice.multiply(BigDecimal.valueOf(quantity));

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("cartId", cart.getCartId());
        response.put("itemId", request.getItemId());
        response.put("quantity", quantity);
        response.put("listPrice", listPrice);
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

        Item item = getItemByItemId(itemId);
        if (item == null) {
            return ApiResponse.notFound("Item not found with id: " + itemId);
        }
        if (getCartItem(username, itemId) == null) {
            return ApiResponse.notFound("Cart item not found with id: " + itemId);
        }

        cartService.updateCartItemQuantity(username, itemId, request.getQuantity());

        BigDecimal listPrice = item.getListPrice() != null ? item.getListPrice() : BigDecimal.ZERO;
        BigDecimal subtotal = listPrice.multiply(BigDecimal.valueOf(request.getQuantity()));

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("itemId", itemId);
        response.put("quantity", request.getQuantity());
        response.put("listPrice", listPrice);
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
        if (getCartItem(username, itemId) == null) {
            return ApiResponse.notFound("Cart item not found with id: " + itemId);
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
