package org.csu.mypetstorebackend.dto;

/**
 * 创建分类请求DTO
 */
public class CreateCategoryRequest {
    private String categoryId;
    private String name;
    private String description;

    public CreateCategoryRequest() {}

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
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

