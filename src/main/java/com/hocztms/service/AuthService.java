package com.hocztms.service;

import com.hocztms.common.RestResult;
import com.hocztms.entity.Users;

import javax.servlet.http.HttpServletRequest;


public interface AuthService {

    RestResult authLogout(HttpServletRequest request);

    RestResult authLogin(String username, String password);

    RestResult authRegister(Users users);
}
