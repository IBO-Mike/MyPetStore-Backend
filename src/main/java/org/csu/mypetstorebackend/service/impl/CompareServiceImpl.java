package org.csu.mypetstorebackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.csu.mypetstorebackend.entity.Compare;
import org.csu.mypetstorebackend.persistence.CompareMapper;
import org.csu.mypetstorebackend.service.CompareService;
import org.csu.mypetstorebackend.utils.TimeUtil;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("compareService")
public class CompareServiceImpl implements CompareService {
    private final CompareMapper compareMapper;

    public CompareServiceImpl(CompareMapper compareMapper) {
        this.compareMapper = compareMapper;
    }

    @Override
    public Compare addCompare(String userId, String productId) {
        if (userId == null || userId.trim().isEmpty() || productId == null || productId.trim().isEmpty()) {
            return null;
        }

        QueryWrapper<Compare> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userid", userId).eq("productid", productId).orderByDesc("create_time").last("LIMIT 1");
        Compare existing = compareMapper.selectOne(queryWrapper);
        if (existing != null) {
            return existing;
        }

        Compare compare = new Compare();
        compare.setUserId(userId);
        compare.setProductId(productId);
        compare.setCreateTime(TimeUtil.currentMysqlDateTime());

        compareMapper.insert(compare);
        return compare;
    }

    @Override
    public boolean deleteCompare(Integer id) {
        return compareMapper.deleteById(id) > 0;
    }

    @Override
    public boolean deleteCompareByProductId(String userId, String productId) {
        QueryWrapper<Compare> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userid", userId).eq("productid", productId);
        return compareMapper.delete(queryWrapper) > 0;
    }

    @Override
    public boolean clearCompare(String userId) {
        QueryWrapper<Compare> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userid", userId);
        return compareMapper.delete(queryWrapper) > 0;
    }

    @Override
    public List<Compare> getComparesByUserId(String userId) {
        QueryWrapper<Compare> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userid", userId).orderByDesc("create_time");
        return compareMapper.selectList(queryWrapper);
    }

    @Override
    public Compare getCompareById(Integer id) {
        return compareMapper.selectById(id);
    }
}
