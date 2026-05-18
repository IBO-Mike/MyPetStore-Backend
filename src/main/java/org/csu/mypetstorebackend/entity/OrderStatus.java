package org.csu.mypetstorebackend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("orderstatus")
public class OrderStatus {
    @TableId(value = "orderid", type = IdType.INPUT)
    private int orderId;
    @TableField("linenum")
    private int lineNumber;
    @TableField("timestamp")
    private java.util.Date timestamp;
    @TableField("status")
    private String status;
}