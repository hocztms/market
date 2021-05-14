package com.hocztms.entity;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class Users {
    private String username;
    private String password;
    private String email;
    private String phone;
    private int status; //0冻结 1正常使用

}
