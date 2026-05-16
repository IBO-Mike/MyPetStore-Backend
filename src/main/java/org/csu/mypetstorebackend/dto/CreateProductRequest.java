package org.csu.mypetstorebackend.dto;

/**
 * 创建产品请求DTO
 */
public class CreateProductRequest {
    private String categoryId;
    private String productId;
    private String name;
    private String description;

    public CreateProductRequest() {}

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}

