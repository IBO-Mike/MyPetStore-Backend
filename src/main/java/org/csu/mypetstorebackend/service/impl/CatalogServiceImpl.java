package org.csu.mypetstorebackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.csu.mypetstorebackend.common.PageResponse;
import org.csu.mypetstorebackend.entity.Category;
import org.csu.mypetstorebackend.entity.Item;
import org.csu.mypetstorebackend.entity.Product;
import org.csu.mypetstorebackend.persistence.CategoryMapper;
import org.csu.mypetstorebackend.persistence.ItemMapper;
import org.csu.mypetstorebackend.persistence.ProductMapper;
import org.csu.mypetstorebackend.service.CatalogService;
import org.csu.mypetstorebackend.utils.TimeUtil;
import org.springframework.stereotype.Service;
import java.util.Locale;
import java.util.List;

@Service("catalogService")
public class CatalogServiceImpl implements CatalogService {
    private final CategoryMapper categoryMapper;
    private final ProductMapper productMapper;
    private final ItemMapper itemMapper;

    public CatalogServiceImpl(CategoryMapper categoryMapper, ProductMapper productMapper, ItemMapper itemMapper) {
        this.categoryMapper = categoryMapper;
        this.productMapper = productMapper;
        this.itemMapper = itemMapper;
    }

    private String getCurrentTimestamp() {
        return TimeUtil.currentMysqlDateTime();
    }

    private String normalizeSearchValue(String value) {
        return value == null ? "" : value.trim();
    }

    private Product attachItems(Product product) {
        if (product != null) {
            product.setItems(getItemsByProductId(product.getProductId()));
        }
        return product;
    }

    private List<Product> attachItems(List<Product> products) {
        products.forEach(this::attachItems);
        return products;
    }

    // Category Methods
    @Override
    public List<Category> getAllCategories() {
        QueryWrapper<Category> queryWrapper = new QueryWrapper<>();
        return categoryMapper.selectList(queryWrapper);
    }

    @Override
    public Category getCategoryById(String categoryId) {
        return categoryMapper.selectById(categoryId);
    }

    @Override
    public Category createCategory(Category category) {
        categoryMapper.insert(category);
        return category;
    }

    @Override
    public Category updateCategory(String categoryId, Category category) {
        category.setCategoryId(categoryId);
        categoryMapper.updateById(category);
        return category;
    }

    @Override
    public void deleteCategory(String categoryId) {
        categoryMapper.deleteById(categoryId);
    }

    @Override
    public PageResponse<Category> searchCategories(String keyword, int page, int pageSize) {
        Page<Category> pageObj = new Page<>(page, pageSize);
        QueryWrapper<Category> queryWrapper = new QueryWrapper<>();
        queryWrapper.like("name", keyword).or().like("description", keyword);
        Page<Category> result = categoryMapper.selectPage(pageObj, queryWrapper);
        return new PageResponse<>(result.getTotal(), page, pageSize, result.getRecords());
    }

    // Product Methods
    @Override
    public List<Product> getProductsByCategory(String categoryId) {
        QueryWrapper<Product> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("category", categoryId);
        return attachItems(productMapper.selectList(queryWrapper));
    }

    @Override
    public PageResponse<Product> getProductsByCategoryPaged(String categoryId, int page, int pageSize, String sortBy, String order) {
        Page<Product> pageObj = new Page<>(page, pageSize);
        QueryWrapper<Product> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("category", categoryId);
        if (sortBy != null && !sortBy.isEmpty()) {
            if ("desc".equalsIgnoreCase(order)) {
                queryWrapper.orderByDesc(sortBy);
            } else {
                queryWrapper.orderByAsc(sortBy);
            }
        }
        Page<Product> result = productMapper.selectPage(pageObj, queryWrapper);
        return new PageResponse<>(result.getTotal(), page, pageSize, attachItems(result.getRecords()));
    }

    @Override
    public Product getProductById(String productId) {
        QueryWrapper<Product> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("productid", productId);
        return attachItems(productMapper.selectOne(queryWrapper));
    }

    @Override
    public Product createProduct(Product product) {
        productMapper.insert(product);
        return product;
    }

    @Override
    public Product updateProduct(String productId, Product product) {
        product.setProductId(productId);
        QueryWrapper<Product> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("productid", productId);
        productMapper.update(product, queryWrapper);
        return product;
    }

    @Override
    public void deleteProduct(String productId) {
        QueryWrapper<Product> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("productid", productId);
        productMapper.delete(queryWrapper);
    }

    @Override
    public PageResponse<Product> searchProducts(String keyword, String categoryId, int page, int pageSize) {
        Page<Product> pageObj = new Page<>(page, pageSize);
        QueryWrapper<Product> queryWrapper = new QueryWrapper<>();
        String normalizedKeyword = normalizeSearchValue(keyword);
        String normalizedCategoryId = normalizeSearchValue(categoryId);
        if (!normalizedKeyword.isEmpty()) {
            String keywordLower = normalizedKeyword.toLowerCase(Locale.ROOT);
            queryWrapper.and(q -> q
                    .apply("LOWER(productid) LIKE CONCAT('%', {0}, '%')", keywordLower)
                    .or()
                    .apply("LOWER(name) LIKE CONCAT('%', {0}, '%')", keywordLower)
                    .or()
                    .apply("LOWER(descn) LIKE CONCAT('%', {0}, '%')", keywordLower)
                    .or()
                    .apply("LOWER(category) LIKE CONCAT('%', {0}, '%')", keywordLower));
        }
        if (!normalizedCategoryId.isEmpty()) {
            queryWrapper.eq("category", normalizedCategoryId);
        }
        Page<Product> result = productMapper.selectPage(pageObj, queryWrapper);
        return new PageResponse<>(result.getTotal(), page, pageSize, attachItems(result.getRecords()));
    }

    // Item Methods
    @Override
    public Item getItemById(String itemId) {
        QueryWrapper<Item> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("itemid", itemId);
        return itemMapper.selectOne(queryWrapper);
    }

    @Override
    public List<Item> getItemsByProductId(String productId) {
        QueryWrapper<Item> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("productid", productId);
        return itemMapper.selectList(queryWrapper);
    }

    @Override
    public Item createItem(Item item) {
        itemMapper.insert(item);
        return item;
    }

    @Override
    public Item updateItem(String itemId, Item item) {
        item.setItemId(itemId);
        QueryWrapper<Item> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("itemid", itemId);
        itemMapper.update(item, queryWrapper);
        return item;
    }

    @Override
    public void deleteItem(String itemId) {
        QueryWrapper<Item> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("itemid", itemId);
        itemMapper.delete(queryWrapper);
    }

    @Override
    public PageResponse<Item> searchItems(String keyword, String productId, int page, int pageSize) {
        Page<Item> pageObj = new Page<>(page, pageSize);
        QueryWrapper<Item> queryWrapper = new QueryWrapper<>();
        if (keyword != null && !keyword.isEmpty()) {
            queryWrapper.like("itemid", keyword);
        }
        if (productId != null && !productId.isEmpty()) {
            queryWrapper.eq("productid", productId);
        }
        Page<Item> result = itemMapper.selectPage(pageObj, queryWrapper);
        return new PageResponse<>(result.getTotal(), page, pageSize, result.getRecords());
    }
}
