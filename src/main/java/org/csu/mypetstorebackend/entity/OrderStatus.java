package org.csu.mypetstorebackend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("orderstatus")
public class OrderStatus {
    @TableId(type = IdType.AUTO)
    private Integer id;
    @TableField("orderid")
    private int orderId;
    @TableField("linenum")
    private int lineNumber;
    @TableField("timestamp")
    private java.util.Date timestamp;
    @TableField("status")
    private int status;
    @TableField("create_time")
    private String createTime;
    @TableField("update_time")
    private String updateTime;
    private int deleted;
}