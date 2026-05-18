package org.csu.mypetstorebackend.persistence;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.csu.mypetstorebackend.entity.Favorite;
import org.springframework.stereotype.Repository;

@Repository
public interface FavoriteMapper extends BaseMapper<Favorite> {
}
