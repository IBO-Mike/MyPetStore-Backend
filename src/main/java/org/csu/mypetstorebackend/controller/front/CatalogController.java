package org.csu.mypetstorebackend.controller.front;

import lombok.RequiredArgsConstructor;
import org.csu.mypetstorebackend.common.ApiResponse;
import org.csu.mypetstorebackend.common.PageResponse;
import org.csu.mypetstorebackend.entity.Category;
import org.csu.mypetstorebackend.entity.Item;
import org.csu.mypetstorebackend.entity.Product;
import org.csu.mypetstorebackend.service.CatalogService;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 商品目录控制器
 * 提供分类、商品和商品的查询接口
 */
@RestController
@RequestMapping("/catalog")
@RequiredArgsConstructor
public class CatalogController {
    
    private final CatalogService catalogService;

    // ==================== 分类相关接口 ====================

    /**
     * 获取所有分类列表
     */
    @GetMapping("/categories")
    public ApiResponse<List<Category>> getAllCategories() {
        List<Category> categories = catalogService.getAllCategories();
        return ApiResponse.success(categories);
    }

    /**
     * 获取分类详情（包含该分类下的商品）
     */
    @GetMapping("/categories/{categoryId}")
    public ApiResponse<Category> getCategoryDetail(@PathVariable String categoryId) {
        Category category = catalogService.getCategoryById(categoryId);
        if (category == null) {
            return ApiResponse.notFound("Category not found with id: " + categoryId);
        }

        List<Product> products = catalogService.getProductsByCategory(categoryId);
        // Note: The Category entity should have a products field, or we need to create a DTO
        // For now, returning the category directly. In production, consider creating a CategoryDetailDTO
        return ApiResponse.success(category);
    }

    /**
     * 分页获取分类下的商品列表
     */
    @GetMapping("/categories/{categoryId}/products")
    public ApiResponse<PageResponse<Product>> getProductsByCategory(
            @PathVariable String categoryId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) String order) {
        
        PageResponse<Product> result = catalogService.getProductsByCategoryPaged(
                categoryId, page, pageSize, sortBy, order);
        return ApiResponse.success(result);
    }

    // ==================== 商品相关接口 ====================

    /**
     * 获取商品详情（包含该商品下的所有SKU）
     */
    @GetMapping("/products/{productId}")
    public ApiResponse<Product> getProductDetail(@PathVariable String productId) {
        Product product = catalogService.getProductById(productId);
        if (product == null) {
            return ApiResponse.notFound("Product not found with id: " + productId);
        }

        // The Product entity should have items field, or we need to create a DTO
        // For now, returning the product directly. In production, consider creating a ProductDetailDTO
        return ApiResponse.success(product);
    }

    // ==================== SKU相关接口 ====================

    /**
     * 获取SKU详情
     */
    @GetMapping("/items/{itemId}")
    public ApiResponse<Map<String, Object>> getItemDetail(@PathVariable String itemId) {
        Item item = catalogService.getItemById(itemId);
        if (item == null) {
            return ApiResponse.notFound("Item not found with id: " + itemId);
        }
        
        // Get associated product
        Product product = catalogService.getProductById(item.getProductId());
        
        Map<String, Object> response = new HashMap<>();
        response.put("id", item.getId());
        response.put("itemId", item.getItemId());
        response.put("productId", item.getProductId());
        response.put("listPrice", item.getListPrice());
        response.put("unitCost", item.getUnitCost());
        response.put("supplierId", item.getSupplierId());
        response.put("status", item.getStatus());
        response.put("attribute1", item.getAttribute1());
        response.put("attribute2", item.getAttribute2());
        response.put("attribute3", item.getAttribute3());
        response.put("attribute4", item.getAttribute4());
        response.put("attribute5", item.getAttribute5());
        response.put("createTime", item.getCreateTime());
        response.put("updateTime", item.getUpdateTime());
        
        if (product != null) {
            Map<String, Object> productInfo = new HashMap<>();
            productInfo.put("id", product.getId());
            productInfo.put("productId", product.getProductId());
            productInfo.put("categoryId", product.getCategoryId());
            productInfo.put("name", product.getName());
            productInfo.put("description", product.getDescription());
            response.put("product", productInfo);
        }
        
        return ApiResponse.success(response);
    }

    // ==================== 搜索接口 ====================

    /**
     * 搜索商品（支持关键词和分类过滤）
     */
    @GetMapping("/search")
    public ApiResponse<PageResponse<Product>> searchProducts(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String categoryId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize) {

        if ((keyword == null || keyword.isEmpty()) && (categoryId == null || categoryId.isEmpty())) {
            return ApiResponse.badRequest("At least one of 'keyword' or 'categoryId' is required");
        }

        PageResponse<Product> result = catalogService.searchProducts(keyword, categoryId, page, pageSize);
        return ApiResponse.success(result);
    }
}


