package org.csu.mypetstorebackend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;

@Data
@TableName("lineitem")
public class LineItem {
    @TableId(value = "orderid", type = IdType.INPUT)
    private int orderId;
    @TableField("linenum")
    private int lineNumber;
    @TableField("itemid")
    private String itemId;
    @TableField("quantity")
    private int quantity;
    @TableField("unitprice")
    private BigDecimal unitPrice;
}
