package org.csu.mypetstorebackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.csu.mypetstorebackend.entity.Favorite;
import org.csu.mypetstorebackend.persistence.FavoriteMapper;
import org.csu.mypetstorebackend.service.FavoriteService;
import org.csu.mypetstorebackend.utils.TimeUtil;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("favoriteService")
public class FavoriteServiceImpl implements FavoriteService {
    private final FavoriteMapper favoriteMapper;

    public FavoriteServiceImpl(FavoriteMapper favoriteMapper) {
        this.favoriteMapper = favoriteMapper;
    }

    private String getCurrentTimestamp() {
        return TimeUtil.currentMysqlDateTime();
    }

    @Override
    public Favorite addFavorite(String userId, String productId) {
        if (userId == null || userId.trim().isEmpty() || productId == null || productId.trim().isEmpty()) {
            return null;
        }

        QueryWrapper<Favorite> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userid", userId).eq("productid", productId);
        Favorite existing = favoriteMapper.selectOne(queryWrapper);
        if (existing != null) {
            return existing;
        }

        Favorite favorite = new Favorite();
        favorite.setUserId(userId);
        favorite.setProductId(productId);

        favoriteMapper.insert(favorite);
        return favorite;
    }

    @Override
    public boolean deleteFavorite(Integer id) {
        return favoriteMapper.deleteById(id) > 0;
    }

    @Override
    public List<Favorite> getFavoritesByUserId(String userId) {
        QueryWrapper<Favorite> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userid", userId).orderByDesc("create_time");
        return favoriteMapper.selectList(queryWrapper);
    }

    @Override
    public Favorite getFavoriteById(Integer id) {
        return favoriteMapper.selectById(id);
    }
}
