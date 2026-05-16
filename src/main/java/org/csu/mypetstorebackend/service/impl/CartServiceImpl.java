package org.csu.mypetstorebackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.csu.mypetstorebackend.entity.Cart;
import org.csu.mypetstorebackend.entity.CartItem;
import org.csu.mypetstorebackend.persistence.CartItemMapper;
import org.csu.mypetstorebackend.persistence.CartMapper;
import org.csu.mypetstorebackend.service.CartService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Random;

@Service("cartService")
public class CartServiceImpl implements CartService {
    private final CartMapper cartMapper;
    private final CartItemMapper cartItemMapper;

    public CartServiceImpl(CartMapper cartMapper, CartItemMapper cartItemMapper) {
        this.cartMapper = cartMapper;
        this.cartItemMapper = cartItemMapper;
    }

    private String getCurrentTimestamp() {
        return LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME) + "Z";
    }

    @Override
    public Cart getCartByUserId(String userId) {
        QueryWrapper<Cart> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        Cart cart = cartMapper.selectOne(queryWrapper);
        
        if (cart == null) {
            // Create cart if it doesn't exist
            cart = createCart(userId);
        }
        
        return cart;
    }

    @Override
    public Cart createCart(String userId) {
        Cart cart = new Cart();
        cart.setUserId(userId);
        cart.setCartId(new Random().nextInt(Integer.MAX_VALUE));
        cart.setCreateTime(getCurrentTimestamp());
        cart.setUpdateTime(getCurrentTimestamp());
        cartMapper.insert(cart);
        return cart;
    }

    @Override
    public void addItemToCart(String userId, String itemId, int quantity) {
        Cart cart = getCartByUserId(userId);
        
        QueryWrapper<CartItem> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("cart_id", cart.getCartId())
                   .eq("item_id", itemId);
        CartItem existingItem = cartItemMapper.selectOne(queryWrapper);
        
        if (existingItem != null) {
            // Update quantity if item already exists
            existingItem.setQuantity(existingItem.getQuantity() + quantity);
            existingItem.setUpdateTime(getCurrentTimestamp());
            cartItemMapper.updateById(existingItem);
        } else {
            // Add new item to cart
            CartItem cartItem = new CartItem();
            cartItem.setCartId(cart.getCartId());
            cartItem.setItemId(itemId);
            cartItem.setQuantity(quantity);
            cartItem.setIsInStock(1);
            cartItem.setCreateTime(getCurrentTimestamp());
            cartItem.setUpdateTime(getCurrentTimestamp());
            cartItemMapper.insert(cartItem);
        }
        
        cart.setUpdateTime(getCurrentTimestamp());
        cartMapper.updateById(cart);
    }

    @Override
    public void updateCartItemQuantity(String userId, String itemId, int quantity) {
        Cart cart = getCartByUserId(userId);
        
        QueryWrapper<CartItem> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("cart_id", cart.getCartId())
                   .eq("item_id", itemId);
        CartItem cartItem = cartItemMapper.selectOne(queryWrapper);
        
        if (cartItem != null) {
            cartItem.setQuantity(quantity);
            cartItem.setUpdateTime(getCurrentTimestamp());
            cartItemMapper.updateById(cartItem);
            
            cart.setUpdateTime(getCurrentTimestamp());
            cartMapper.updateById(cart);
        }
    }

    @Override
    public void removeItemFromCart(String userId, String itemId) {
        Cart cart = getCartByUserId(userId);
        
        QueryWrapper<CartItem> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("cart_id", cart.getCartId())
                   .eq("item_id", itemId);
        cartItemMapper.delete(queryWrapper);
        
        cart.setUpdateTime(getCurrentTimestamp());
        cartMapper.updateById(cart);
    }

    @Override
    public void clearCart(String userId) {
        Cart cart = getCartByUserId(userId);
        
        QueryWrapper<CartItem> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("cart_id", cart.getCartId());
        cartItemMapper.delete(queryWrapper);
        
        cart.setUpdateTime(getCurrentTimestamp());
        cartMapper.updateById(cart);
    }

    @Override
    public List<CartItem> getCartItems(String userId) {
        Cart cart = getCartByUserId(userId);
        
        QueryWrapper<CartItem> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("cart_id", cart.getCartId());
        return cartItemMapper.selectList(queryWrapper);
    }
}

