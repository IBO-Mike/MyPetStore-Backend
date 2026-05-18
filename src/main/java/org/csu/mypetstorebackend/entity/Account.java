package org.csu.mypetstorebackend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("account")
public class Account {
    @TableId(value = "userid", type = IdType.INPUT)
    private String username;
    private String email;
    @TableField("firstname")
    private String firstName;
    @TableField("lastname")
    private String lastName;
    private String status;
    @TableField("addr1")
    private String address1;
    @TableField("addr2")
    private String address2;
    private String city;
    private String state;
    private String zip;
    private String country;
    private String phone;
    @TableField(exist = false)
    private String languagePreference;
    @TableField(exist = false)
    private String favoriteCategory;
    @TableField(exist = false)
    private int myListOption;
    @TableField(exist = false)
    private int bannerOption;
    @TableField(exist = false)
    private String password;
}
