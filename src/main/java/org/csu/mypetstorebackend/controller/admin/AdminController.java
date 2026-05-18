package org.csu.mypetstorebackend.controller.admin;

import org.csu.mypetstorebackend.common.ApiResponse;
import org.csu.mypetstorebackend.common.PageResponse;
import org.csu.mypetstorebackend.dto.*;
import org.csu.mypetstorebackend.entity.*;
import org.csu.mypetstorebackend.persistence.AccountMapper;
import org.csu.mypetstorebackend.persistence.SignonMapper;
import org.csu.mypetstorebackend.service.CatalogService;
import org.csu.mypetstorebackend.service.OrderService;
import org.csu.mypetstorebackend.utils.JwtUtil;
import org.csu.mypetstorebackend.utils.TimeUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin")
public class AdminController {
    private final CatalogService catalogService;
    private final OrderService orderService;
    private final AccountMapper accountMapper;
    private final SignonMapper signonMapper;

    public AdminController(CatalogService catalogService, OrderService orderService, AccountMapper accountMapper, SignonMapper signonMapper) {
        this.catalogService = catalogService;
        this.orderService = orderService;
        this.accountMapper = accountMapper;
        this.signonMapper = signonMapper;
    }

    private String extractUsernameFromToken(String token) {
        if (token == null || !token.startsWith("Bearer ")) {
            return null;
        }
        String actualToken = token.substring(7);
        if (!JwtUtil.validateToken(actualToken)) {
            return null;
        }
        return JwtUtil.extractUsername(actualToken);
    }

    private ApiResponse<?> checkAdminAuth(String token, String adminRole) {
        String username = extractUsernameFromToken(token);
        if (username == null) {
            return ApiResponse.unauthorized("Authentication required.");
        }
        
        // Note: In production, check actual admin role from database
        if (!"admin".equals(adminRole)) {
            return ApiResponse.forbidden("You do not have permission to perform this action.");
        }
        
        return null;
    }

    // Dashboard
    @GetMapping("/dashboard")
    public ApiResponse<Object> getDashboard(
            @RequestHeader(value = "Authorization", required = false) String token,
            @RequestHeader(value = "X-Admin-Role", required = false) String adminRole) {
        ApiResponse<?> authCheck = checkAdminAuth(token, adminRole);
        if (authCheck != null) return apiResponseFromAuthCheck(authCheck);

        // Get statistics
        Object stats = new Object() {
            public int totalOrders = 250;
            public double totalRevenue = 12500.50;
            public int pendingOrders = 15;
            public int totalUsers = 100;
            public int totalProducts = 500;
            public int totalItems = 1200;
        };

        return ApiResponse.success(stats);
    }

    // Category Management
    @PostMapping("/categories")
    public ApiResponse<Object> createCategory(
            @RequestHeader(value = "Authorization", required = false) String token,
            @RequestHeader(value = "X-Admin-Role", required = false) String adminRole,
            @RequestBody CreateCategoryRequest request) {
        ApiResponse<?> authCheck = checkAdminAuth(token, adminRole);
        if (authCheck != null) return apiResponseFromAuthCheck(authCheck);

        Category category = new Category();
        category.setCategoryId(request.getCategoryId());
        category.setName(request.getName());
        category.setDescription(request.getDescription());

        Category created = catalogService.createCategory(category);
        return ApiResponse.created("Category created successfully", created);
    }

    @PutMapping("/categories/{categoryId}")
    public ApiResponse<Object> updateCategory(
            @PathVariable String categoryId,
            @RequestHeader(value = "Authorization", required = false) String token,
            @RequestHeader(value = "X-Admin-Role", required = false) String adminRole,
            @RequestBody CreateCategoryRequest request) {
        ApiResponse<?> authCheck = checkAdminAuth(token, adminRole);
        if (authCheck != null) return apiResponseFromAuthCheck(authCheck);

        Category category = new Category();
        category.setCategoryId(categoryId);
        category.setName(request.getName());
        category.setDescription(request.getDescription());

        Category updated = catalogService.updateCategory(categoryId, category);
        return ApiResponse.success("Category updated successfully", updated);
    }

    @DeleteMapping("/categories/{categoryId}")
    public ApiResponse<Object> deleteCategory(
            @PathVariable String categoryId,
            @RequestHeader(value = "Authorization", required = false) String token,
            @RequestHeader(value = "X-Admin-Role", required = false) String adminRole) {
        ApiResponse<?> authCheck = checkAdminAuth(token, adminRole);
        if (authCheck != null) return apiResponseFromAuthCheck(authCheck);

        catalogService.deleteCategory(categoryId);
        return ApiResponse.success("Category deleted successfully", null);
    }

    @GetMapping("/categories/search")
    public ApiResponse<Object> searchCategories(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestHeader(value = "Authorization", required = false) String token,
            @RequestHeader(value = "X-Admin-Role", required = false) String adminRole) {
        ApiResponse<?> authCheck = checkAdminAuth(token, adminRole);
        if (authCheck != null) return apiResponseFromAuthCheck(authCheck);

        if (keyword == null || keyword.isEmpty()) {
            return ApiResponse.badRequest("Search keyword is required");
        }

        PageResponse<Category> result = catalogService.searchCategories(keyword, page, pageSize);
        return ApiResponse.success(result);
    }

    // Product Management
    @PostMapping("/products")
    public ApiResponse<Object> createProduct(
            @RequestHeader(value = "Authorization", required = false) String token,
            @RequestHeader(value = "X-Admin-Role", required = false) String adminRole,
            @RequestBody CreateProductRequest request) {
        ApiResponse<?> authCheck = checkAdminAuth(token, adminRole);
        if (authCheck != null) return apiResponseFromAuthCheck(authCheck);

        Product product = new Product();
        product.setProductId(request.getProductId());
        product.setCategoryId(request.getCategoryId());
        product.setName(request.getName());
        product.setDescription(request.getDescription());

        Product created = catalogService.createProduct(product);
        return ApiResponse.created("Product created successfully", created);
    }

    @PutMapping("/products/{productId}")
    public ApiResponse<Object> updateProduct(
            @PathVariable String productId,
            @RequestHeader(value = "Authorization", required = false) String token,
            @RequestHeader(value = "X-Admin-Role", required = false) String adminRole,
            @RequestBody CreateProductRequest request) {
        ApiResponse<?> authCheck = checkAdminAuth(token, adminRole);
        if (authCheck != null) return apiResponseFromAuthCheck(authCheck);

        Product product = new Product();
        product.setProductId(productId);
        product.setName(request.getName());
        product.setDescription(request.getDescription());

        Product updated = catalogService.updateProduct(productId, product);
        return ApiResponse.success("Product updated successfully", updated);
    }

    @DeleteMapping("/products/{productId}")
    public ApiResponse<Object> deleteProduct(
            @PathVariable String productId,
            @RequestHeader(value = "Authorization", required = false) String token,
            @RequestHeader(value = "X-Admin-Role", required = false) String adminRole) {
        ApiResponse<?> authCheck = checkAdminAuth(token, adminRole);
        if (authCheck != null) return apiResponseFromAuthCheck(authCheck);

        catalogService.deleteProduct(productId);
        return ApiResponse.success("Product deleted successfully", null);
    }

    @GetMapping("/products/search")
    public ApiResponse<Object> searchProducts(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String categoryId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestHeader(value = "Authorization", required = false) String token,
            @RequestHeader(value = "X-Admin-Role", required = false) String adminRole) {
        ApiResponse<?> authCheck = checkAdminAuth(token, adminRole);
        if (authCheck != null) return apiResponseFromAuthCheck(authCheck);

        if (keyword == null || keyword.isEmpty()) {
            return ApiResponse.badRequest("Search keyword is required");
        }

        PageResponse<Product> result = catalogService.searchProducts(keyword, categoryId, page, pageSize);
        return ApiResponse.success(result);
    }

    // Item Management
    @PostMapping("/items")
    public ApiResponse<Object> createItem(
            @RequestHeader(value = "Authorization", required = false) String token,
            @RequestHeader(value = "X-Admin-Role", required = false) String adminRole,
            @RequestBody CreateItemRequest request) {
        ApiResponse<?> authCheck = checkAdminAuth(token, adminRole);
        if (authCheck != null) return apiResponseFromAuthCheck(authCheck);

        Item item = new Item();
        item.setProductId(request.getProductId());
        item.setItemId(request.getItemId());
        item.setListPrice(request.getListPrice());
        item.setUnitCost(request.getUnitCost());
        item.setSupplierId(request.getSupplierId());
        item.setStatus(request.getStatus());
        item.setAttribute1(request.getAttribute1());
        item.setAttribute2(request.getAttribute2());
        item.setAttribute3(request.getAttribute3());
        item.setAttribute4(request.getAttribute4());
        item.setAttribute5(request.getAttribute5());

        Item created = catalogService.createItem(item);
        return ApiResponse.created("Item created successfully", created);
    }

    @PutMapping("/items/{itemId}")
    public ApiResponse<Object> updateItem(
            @PathVariable String itemId,
            @RequestHeader(value = "Authorization", required = false) String token,
            @RequestHeader(value = "X-Admin-Role", required = false) String adminRole,
            @RequestBody CreateItemRequest request) {
        ApiResponse<?> authCheck = checkAdminAuth(token, adminRole);
        if (authCheck != null) return apiResponseFromAuthCheck(authCheck);

        Item item = new Item();
        item.setItemId(itemId);
        item.setListPrice(request.getListPrice());
        item.setUnitCost(request.getUnitCost());
        item.setStatus(request.getStatus());

        Item updated = catalogService.updateItem(itemId, item);
        return ApiResponse.success("Item updated successfully", updated);
    }

    @DeleteMapping("/items/{itemId}")
    public ApiResponse<Object> deleteItem(
            @PathVariable String itemId,
            @RequestHeader(value = "Authorization", required = false) String token,
            @RequestHeader(value = "X-Admin-Role", required = false) String adminRole) {
        ApiResponse<?> authCheck = checkAdminAuth(token, adminRole);
        if (authCheck != null) return apiResponseFromAuthCheck(authCheck);

        catalogService.deleteItem(itemId);
        return ApiResponse.success("Item deleted successfully", null);
    }

    @GetMapping("/items/search")
    public ApiResponse<Object> searchItems(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String productId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestHeader(value = "Authorization", required = false) String token,
            @RequestHeader(value = "X-Admin-Role", required = false) String adminRole) {
        ApiResponse<?> authCheck = checkAdminAuth(token, adminRole);
        if (authCheck != null) return apiResponseFromAuthCheck(authCheck);

        if (keyword == null || keyword.isEmpty()) {
            return ApiResponse.badRequest("Search keyword is required");
        }

        PageResponse<Item> result = catalogService.searchItems(keyword, productId, page, pageSize);
        return ApiResponse.success(result);
    }

    // Order Management
    @GetMapping("/orders")
    public ApiResponse<PageResponse<Orders>> getAllOrders(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String userId,
            @RequestHeader(value = "Authorization", required = false) String token,
            @RequestHeader(value = "X-Admin-Role", required = false) String adminRole) {
        ApiResponse<?> authCheck = checkAdminAuth(token, adminRole);
        if (authCheck != null) return apiResponseFromAuthCheck(authCheck);

        PageResponse<Orders> result = orderService.getAllOrders(page, pageSize, status, userId);
        return ApiResponse.success(result);
    }

    private <T> ApiResponse<T> apiResponseFromAuthCheck(ApiResponse<?> authCheck) {
        return (ApiResponse<T>) authCheck;
    }

    @PostMapping("/orders/{orderId}/ship")
    public ApiResponse<Object> shipOrder(
            @PathVariable int orderId,
            @RequestHeader(value = "Authorization", required = false) String token,
            @RequestHeader(value = "X-Admin-Role", required = false) String adminRole) {
        ApiResponse<?> authCheck = checkAdminAuth(token, adminRole);
        if (authCheck != null) return apiResponseFromAuthCheck(authCheck);

        Orders order = orderService.getOrderById(orderId);
        if (order == null) {
            return ApiResponse.notFound("Order not found");
        }

        orderService.shipOrder(orderId);

        Object response = new Object() {
            public int orderId = order.getOrderId();
            public String status = "shipped";
        };

        return ApiResponse.success("Order shipped successfully", response);
    }

    @DeleteMapping("/orders/{orderId}")
    public ApiResponse<Object> deleteOrder(
            @PathVariable int orderId,
            @RequestHeader(value = "Authorization", required = false) String token,
            @RequestHeader(value = "X-Admin-Role", required = false) String adminRole) {
        ApiResponse<?> authCheck = checkAdminAuth(token, adminRole);
        if (authCheck != null) return apiResponseFromAuthCheck(authCheck);

        orderService.deleteOrder(orderId);
        return ApiResponse.success("Order deleted successfully", null);
    }

    @GetMapping("/orders/search")
    public ApiResponse<Object> searchOrders(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestHeader(value = "Authorization", required = false) String token,
            @RequestHeader(value = "X-Admin-Role", required = false) String adminRole) {
        ApiResponse<?> authCheck = checkAdminAuth(token, adminRole);
        if (authCheck != null) return apiResponseFromAuthCheck(authCheck);

        if (keyword == null || keyword.isEmpty()) {
            return ApiResponse.badRequest("Search keyword is required");
        }

        PageResponse<Orders> result = orderService.searchOrders(keyword, page, pageSize);
        return ApiResponse.success(result);
    }

    // User Management
    @GetMapping("/users")
    public ApiResponse<PageResponse<Account>> getAllUsers(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String status,
            @RequestHeader(value = "Authorization", required = false) String token,
            @RequestHeader(value = "X-Admin-Role", required = false) String adminRole) {
        ApiResponse<?> authCheck = checkAdminAuth(token, adminRole);
        if (authCheck != null) return apiResponseFromAuthCheck(authCheck);

        Page<Account> pageObj = new Page<>(page, pageSize);
        QueryWrapper<Account> queryWrapper = new QueryWrapper<>();
        if (status != null && !status.isEmpty()) {
            queryWrapper.eq("status", status);
        }
        Page<Account> result = accountMapper.selectPage(pageObj, queryWrapper);
        PageResponse<Account> response = new PageResponse<>(result.getTotal(), page, pageSize, result.getRecords());
        return ApiResponse.success(response);
    }

    @GetMapping("/users/{username}")
    public ApiResponse<Account> getUserDetail(
            @PathVariable String username,
            @RequestHeader(value = "Authorization", required = false) String token,
            @RequestHeader(value = "X-Admin-Role", required = false) String adminRole) {
        ApiResponse<?> authCheck = checkAdminAuth(token, adminRole);
        if (authCheck != null) return apiResponseFromAuthCheck(authCheck);

        QueryWrapper<Account> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userid", username);
        Account account = accountMapper.selectOne(queryWrapper);

        if (account == null) {
            return ApiResponse.notFound("User not found");
        }

        return ApiResponse.success(account);
    }

    @GetMapping("/users/search")
    public ApiResponse<Object> searchUsers(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestHeader(value = "Authorization", required = false) String token,
            @RequestHeader(value = "X-Admin-Role", required = false) String adminRole) {
        ApiResponse<?> authCheck = checkAdminAuth(token, adminRole);
        if (authCheck != null) return apiResponseFromAuthCheck(authCheck);

        if (keyword == null || keyword.isEmpty()) {
            return ApiResponse.badRequest("Search keyword is required");
        }

        Page<Account> pageObj = new Page<>(page, pageSize);
        QueryWrapper<Account> queryWrapper = new QueryWrapper<>();
        queryWrapper.like("userid", keyword).or().like("email", keyword);
        Page<Account> result = accountMapper.selectPage(pageObj, queryWrapper);
        PageResponse<Account> response = new PageResponse<>(result.getTotal(), page, pageSize, result.getRecords());
        return ApiResponse.success(response);
    }

    @PostMapping("/users/{username}/reset-password")
    public ApiResponse<Object> resetPassword(
            @PathVariable String username,
            @RequestHeader(value = "Authorization", required = false) String token,
            @RequestHeader(value = "X-Admin-Role", required = false) String adminRole,
            @RequestBody Map<String, String> request) {
        ApiResponse<?> authCheck = checkAdminAuth(token, adminRole);
        if (authCheck != null) return apiResponseFromAuthCheck(authCheck);

        QueryWrapper<Account> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userid", username);
        Account account = accountMapper.selectOne(queryWrapper);

        if (account == null) {
            return ApiResponse.notFound("User not found");
        }

        String newPassword = request.get("newPassword");
        if (newPassword == null || newPassword.isEmpty()) {
            return ApiResponse.badRequest("New password is required");
        }

        QueryWrapper<Signon> signonQuery = new QueryWrapper<>();
        signonQuery.eq("username", username);
        Signon signon = signonMapper.selectOne(signonQuery);

        if (signon == null) {
            signon = new Signon();
            signon.setUsername(username);
            signon.setPassword(newPassword);
            signonMapper.insert(signon);
        } else {
            UpdateWrapper<Signon> updateWrapper = new UpdateWrapper<>();
            updateWrapper.eq("username", username).set("password", newPassword);
            signonMapper.update(null, updateWrapper);
        }

        Object response = new Object() {
            public String updatedUsername = username;
            public String message = "Password has been reset to default value";
        };

        return ApiResponse.success("Password reset successfully", response);
    }
}
