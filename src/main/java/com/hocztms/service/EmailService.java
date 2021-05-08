package com.hocztms.service;

import com.hocztms.common.RestResult;
import com.hocztms.entity.Email;
import com.hocztms.vo.UserVo;

import javax.servlet.http.HttpSession;

public interface EmailService {
    RestResult sendPasswordEmail(String username, String emailAddress);

    void sendCheckGoodsEmail(Long goodsId, int tag);

    RestResult sendRegisterEmailCode(String email, HttpSession httpSession);

    RestResult getUpdateEmailCode(String username);

}
