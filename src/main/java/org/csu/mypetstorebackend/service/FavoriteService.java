package org.csu.mypetstorebackend.service;

import org.csu.mypetstorebackend.entity.Favorite;

import java.util.List;

public interface FavoriteService {
    Favorite addFavorite(String userId, String productId);
    boolean deleteFavorite(Integer id);
    List<Favorite> getFavoritesByUserId(String userId);
    Favorite getFavoriteById(Integer id);
}
