package com.hocztms.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Users {
    private String username;
    private String password;
    private String email;
    private String phone;
    private int status; //0冻结 1正常使用
    @TableField(value = "last_loginDate")
    private Date lastLoginDate;

}
