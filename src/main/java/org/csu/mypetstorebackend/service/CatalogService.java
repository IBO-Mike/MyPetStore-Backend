package org.csu.mypetstorebackend.service;

import org.csu.mypetstorebackend.entity.Category;
import org.csu.mypetstorebackend.entity.Item;
import org.csu.mypetstorebackend.entity.Product;
import org.csu.mypetstorebackend.common.PageResponse;

import java.util.List;

public interface CatalogService {
    // Category methods
    List<Category> getAllCategories();
    Category getCategoryById(String categoryId);
    Category createCategory(Category category);
    Category updateCategory(String categoryId, Category category);
    void deleteCategory(String categoryId);
    PageResponse<Category> searchCategories(String keyword, int page, int pageSize);

    // Product methods
    List<Product> getProductsByCategory(String categoryId);
    PageResponse<Product> getProductsByCategoryPaged(String categoryId, int page, int pageSize, String sortBy, String order);
    Product getProductById(String productId);
    Product createProduct(Product product);
    Product updateProduct(String productId, Product product);
    void deleteProduct(String productId);
    PageResponse<Product> searchProducts(String keyword, String categoryId, int page, int pageSize);

    // Item methods
    Item getItemById(String itemId);
    List<Item> getItemsByProductId(String productId);
    Item createItem(Item item);
    Item updateItem(String itemId, Item item);
    void deleteItem(String itemId);
}


