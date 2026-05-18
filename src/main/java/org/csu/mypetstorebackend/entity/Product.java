package org.csu.mypetstorebackend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.List;

@Data
@TableName("product")
public class Product {
    @TableId(value = "productid", type = IdType.INPUT)
    private String productId;
    @TableField(value = "category")
    private String categoryId;
    private String name;
    @TableField(value = "descn")
    private String description;

    @TableField(exist = false)
    private List<Item> items;
}
