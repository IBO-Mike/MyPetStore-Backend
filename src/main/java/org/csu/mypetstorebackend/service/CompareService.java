package org.csu.mypetstorebackend.service;

import org.csu.mypetstorebackend.entity.Compare;

import java.util.List;

public interface CompareService {
    Compare addCompare(String userId, String productId);
    boolean deleteCompare(Integer id);
    boolean deleteCompareByProductId(String userId, String productId);
    boolean clearCompare(String userId);
    List<Compare> getComparesByUserId(String userId);
    Compare getCompareById(Integer id);
}
