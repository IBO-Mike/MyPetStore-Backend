package org.csu.mypetstorebackend.controller.favorite;

import org.csu.mypetstorebackend.common.ApiResponse;
import org.csu.mypetstorebackend.dto.CreateFavoriteRequest;
import org.csu.mypetstorebackend.entity.Favorite;
import org.csu.mypetstorebackend.service.FavoriteService;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/favorites")
public class FavoriteController {
    private final FavoriteService favoriteService;

    public FavoriteController(FavoriteService favoriteService) {
        this.favoriteService = favoriteService;
    }

    @PostMapping
    public ApiResponse<Object> createFavorite(@RequestBody CreateFavoriteRequest request) {
        if (request.getUserId() == null || request.getUserId().trim().isEmpty()) {
            return ApiResponse.badRequest("User ID is required");
        }
        if (request.getProductId() == null || request.getProductId().trim().isEmpty()) {
            return ApiResponse.badRequest("Product ID is required");
        }

        Favorite favorite = favoriteService.addFavorite(request.getUserId(), request.getProductId());

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("id", favorite.getId());
        response.put("userId", favorite.getUserId());
        response.put("productId", favorite.getProductId());
        response.put("createTime", favorite.getCreateTime());

        return ApiResponse.created("Favorite added successfully", response);
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteFavorite(@PathVariable Integer id) {
        Favorite favorite = favoriteService.getFavoriteById(id);
        if (favorite == null) {
            return ApiResponse.notFound("Favorite not found with id: " + id);
        }

        favoriteService.deleteFavorite(id);
        return ApiResponse.success("Favorite deleted successfully", null);
    }

    @GetMapping("/users/{userId}")
    public ApiResponse<Object> getUserFavorites(@PathVariable String userId) {
        if (userId == null || userId.trim().isEmpty()) {
            return ApiResponse.badRequest("User ID is required");
        }

        List<Favorite> favorites = favoriteService.getFavoritesByUserId(userId);

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("userId", userId);
        response.put("favorites", favorites);
        response.put("total", favorites.size());

        return ApiResponse.success(response);
    }
}
