package org.csu.mypetstorebackend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("compare")
public class Compare {
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    @TableField("userid")
    private String userId;
    @TableField("productid")
    private String productId;
    @TableField("create_time")
    private String createTime;
}
