package org.csu.mypetstorebackend.dto;

/**
 * 购物车物品请求DTO
 */
public class CartItemRequest {
    private String itemId;
    private int quantity;

    public CartItemRequest() {}

    public CartItemRequest(String itemId, int quantity) {
        this.itemId = itemId;
        this.quantity = quantity;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}

