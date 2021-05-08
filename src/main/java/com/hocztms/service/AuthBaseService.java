package com.hocztms.service;

import com.hocztms.common.RestResult;

import javax.servlet.http.HttpServletRequest;


public interface AuthBaseService {

    RestResult authLogout(HttpServletRequest request);
}
