package com.hocztms.service;

import com.hocztms.common.RestResult;
import com.hocztms.entity.*;
import com.hocztms.vo.PasswordEmail;
import com.hocztms.vo.UpdateEmailVo;

import java.util.List;

public interface UserService {


    Users findUsersByUsername(String username);

    RestResult ReUserPasswordBySecret(PasswordEmail passwordEmail);

    Integer insertUser(Users users);

    RestResult updateUserEmailByEmailCode(String username, UpdateEmailVo updateEmailVo);

    RestResult updateUserPhoneByUsername(String username, String phone);

    Users findUsersByEmail(String email);

    Users findUsersByPhone(String phone);

    List<Role> getUserRoles(String username);

    RestResult getUserGoods(String username);

    RestResult getUserNormalGoods(long page, long size, String username);

    RestResult getUserIllegalGoods(long page, long size, String username);

    Integer updateUser(Users users);

    Integer updateUserStatusByUsername(String username, int status);

    String userContactToStringByUsername(String username);

}
