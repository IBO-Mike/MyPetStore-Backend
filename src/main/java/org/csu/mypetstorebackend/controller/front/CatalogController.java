package org.csu.mypetstorebackend.controller.front;

import org.csu.mypetstorebackend.common.ApiResponse;
import org.csu.mypetstorebackend.common.PageResponse;
import org.csu.mypetstorebackend.entity.Category;
import org.csu.mypetstorebackend.entity.Item;
import org.csu.mypetstorebackend.entity.Product;
import org.csu.mypetstorebackend.service.CatalogService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/catalog")
public class CatalogController {
    private final CatalogService catalogService;

    public CatalogController(CatalogService catalogService) {
        this.catalogService = catalogService;
    }

    // Category endpoints
    @GetMapping("/categories")
    public ApiResponse<List<Category>> getAllCategories() {
        List<Category> categories = catalogService.getAllCategories();
        return ApiResponse.success(categories);
    }

    @GetMapping("/categories/{categoryId}")
    public ApiResponse<Object> getCategoryDetail(@PathVariable String categoryId) {
        Category category = catalogService.getCategoryById(categoryId);
        if (category == null) {
            return ApiResponse.notFound("Category not found");
        }

        // Get products in this category
        List<Product> products = catalogService.getProductsByCategory(categoryId);

        Object response = new Object() {
            public String id = category.getCategoryId();
            public String categoryId = category.getCategoryId();
            public String name = category.getName();
            public String description = category.getDescription();
            public List<Product> products = products;
        };

        return ApiResponse.success(response);
    }

    @GetMapping("/categories/{categoryId}/products")
    public ApiResponse<PageResponse<Product>> getProductsByCategory(
            @PathVariable String categoryId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) String order) {

        PageResponse<Product> result = catalogService.getProductsByCategoryPaged(categoryId, page, pageSize, sortBy, order);
        return ApiResponse.success(result);
    }

    // Product endpoints
    @GetMapping("/products/{productId}")
    public ApiResponse<Object> getProductDetail(@PathVariable String productId) {
        Product product = catalogService.getProductById(productId);
        if (product == null) {
            return ApiResponse.notFound("Product not found");
        }

        List<Item> productItems = catalogService.getItemsByProductId(productId);

        Object response = new Object() {
            public int id = product.getId();
            public String productIdValue = product.getProductId();
            public String categoryId = product.getCategoryId();
            public String name = product.getName();
            public String description = product.getDescription();
            public List<Item> items = productItems;
            public String createTime = product.getCreateTime();
            public String updateTime = product.getUpdateTime();
        };

        return ApiResponse.success(response);
    }

    // Item endpoints
    @GetMapping("/items/{itemId}")
    public ApiResponse<Item> getItem(@PathVariable String itemId) {
        Item item = catalogService.getItemById(itemId);
        if (item == null) {
            return ApiResponse.notFound("Item not found");
        }
        return ApiResponse.success(item);
    }

    // Search endpoint
    @GetMapping("/search")
    public ApiResponse<PageResponse<Product>> searchProducts(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String categoryId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize) {

        if (keyword == null || keyword.isEmpty()) {
            return ApiResponse.badRequest("Search keyword is required");
        }

        PageResponse<Product> result = catalogService.searchProducts(keyword, categoryId, page, pageSize);
        return ApiResponse.success(result);
    }
}


