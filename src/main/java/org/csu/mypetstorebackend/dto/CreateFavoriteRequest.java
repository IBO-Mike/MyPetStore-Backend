package org.csu.mypetstorebackend.dto;

public class CreateFavoriteRequest {
    private String userId;
    private String productId;

    public CreateFavoriteRequest() {}

    public CreateFavoriteRequest(String userId, String productId) {
        this.userId = userId;
        this.productId = productId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }
}
