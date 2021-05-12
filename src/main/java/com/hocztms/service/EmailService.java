package com.hocztms.service;

import com.hocztms.common.RestResult;

import javax.servlet.http.HttpSession;

public interface EmailService {
    RestResult sendPasswordEmail(String email);

    void sendCheckGoodsEmail(Long goodsId, int tag);

    RestResult sendRegisterEmailCode(String email, HttpSession httpSession);

    RestResult getUpdateEmailCode(String username);

}
