package org.csu.mypetstorebackend.controller.compare;

import org.csu.mypetstorebackend.common.ApiResponse;
import org.csu.mypetstorebackend.dto.CreateCompareRequest;
import org.csu.mypetstorebackend.entity.Compare;
import org.csu.mypetstorebackend.service.CompareService;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/compares")
public class CompareController {
    private final CompareService compareService;

    public CompareController(CompareService compareService) {
        this.compareService = compareService;
    }

    @PostMapping
    public ApiResponse<Object> createCompare(@RequestBody CreateCompareRequest request) {
        if (request.getUserId() == null || request.getUserId().trim().isEmpty()) {
            return ApiResponse.badRequest("User ID is required");
        }
        if (request.getProductId() == null || request.getProductId().trim().isEmpty()) {
            return ApiResponse.badRequest("Product ID is required");
        }

        Compare compare = compareService.addCompare(request.getUserId(), request.getProductId());

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("id", compare.getId());
        response.put("userId", compare.getUserId());
        response.put("productId", compare.getProductId());
        response.put("createTime", compare.getCreateTime());

        return ApiResponse.created("Compare added successfully", response);
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteCompare(@PathVariable Integer id) {
        Compare compare = compareService.getCompareById(id);
        if (compare == null) {
            return ApiResponse.notFound("Compare not found with id: " + id);
        }

        compareService.deleteCompare(id);
        return ApiResponse.success("Compare deleted successfully", null);
    }

    @DeleteMapping("/products/{productId}")
    public ApiResponse<Void> deleteCompareByProductId(
            @PathVariable String productId,
            @RequestParam String userId) {
        compareService.deleteCompareByProductId(userId, productId);
        return ApiResponse.success("Compare deleted successfully", null);
    }

    @DeleteMapping
    public ApiResponse<Void> clearCompare(@RequestParam String userId) {
        compareService.clearCompare(userId);
        return ApiResponse.success("Compare list cleared successfully", null);
    }

    @GetMapping("/users/{userId}")
    public ApiResponse<Object> getUserCompares(@PathVariable String userId) {
        if (userId == null || userId.trim().isEmpty()) {
            return ApiResponse.badRequest("User ID is required");
        }

        List<Compare> compares = compareService.getComparesByUserId(userId);

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("userId", userId);
        response.put("compares", compares);
        response.put("total", compares.size());

        return ApiResponse.success(response);
    }
}
