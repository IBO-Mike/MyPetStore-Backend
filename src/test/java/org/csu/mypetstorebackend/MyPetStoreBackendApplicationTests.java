package org.csu.mypetstorebackend;

import org.csu.mypetstorebackend.entity.Category;
import org.csu.mypetstorebackend.persistence.CategoryMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class MyPetStoreBackendApplicationTests {

    @Autowired
    private CategoryMapper categoryMapper;

    @Test
    void contextLoads() {
    }

    @Test
    void text() {
        List<Category> CategoryList = categoryMapper.selectList(null);
        System.out.println(CategoryList);
        System.out.println(CategoryList.size());
    }
}
