package com.hocztms.service;

import com.hocztms.common.RestResult;
import com.hocztms.entity.*;
import com.hocztms.vo.PasswordEmail;
import com.hocztms.vo.UpdateEmailVo;

import java.util.List;

public interface UserService {


    public Users findUsersByUsername(String username);

    public RestResult ReUserPasswordBySecret(PasswordEmail passwordEmail);

    public Integer insertUser(Users users);

    public RestResult updateUserEmailByEmailCode(String username, UpdateEmailVo updateEmailVo);

    public RestResult updateUserPhoneByUsername(String username, String phone);

    public Users findUsersByEmail(String email);

    public Users findUsersByPhone(String phone);

    public List<Role> getUserRoles (String username);

    public RestResult getUserGoods(String username);

    public Integer updateUser(Users users);

    public Integer updateUserStatusByUsername(String username,int status);

    String userContactToStringByUsername(String username);

}
