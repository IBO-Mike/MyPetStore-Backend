package org.csu.mypetstorebackend.service;

import org.csu.mypetstorebackend.entity.Cart;
import org.csu.mypetstorebackend.entity.CartItem;

import java.util.List;

public interface CartService {
    Cart getCartByUserId(String userId);
    Cart createCart(String userId);
    void addItemToCart(String userId, String itemId, int quantity);
    void updateCartItemQuantity(String userId, String itemId, int quantity);
    void removeItemFromCart(String userId, String itemId);
    void clearCart(String userId);
    List<CartItem> getCartItems(String userId);
}

