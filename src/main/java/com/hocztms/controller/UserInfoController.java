package com.hocztms.controller;


import com.hocztms.common.RestResult;
import com.hocztms.service.EmailService;
import com.hocztms.service.UserService;
import com.hocztms.springSecurity.jwt.JwtAuthService;
import com.hocztms.vo.PhoneVo;
import com.hocztms.vo.UpdateEmailVo;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@RestController
@RequestMapping("/user")
@PreAuthorize("hasAnyAuthority('admin','user')")
public class UserInfoController {

    @Autowired
    private EmailService emailService;
    @Autowired
    private JwtAuthService jwtAuthService;

    @Autowired
    private UserService userService;

    /*
    发送修改邮箱密钥 已测试
     */
    @ApiModelProperty("发送修改邮箱密钥")
    @PostMapping("/getUpdateEmailCode")
    public RestResult getUpdateEmailCode(HttpServletRequest request){
        String username = jwtAuthService.getTokenUsername(request);
        return emailService.getUpdateEmailCode(username);
    }

    /*
    修改邮箱 已测试
     */
    @ApiModelProperty("修改邮箱")
    @PutMapping("/updateEmailByCode")
    public RestResult updateEmailByCode(@Valid @RequestBody UpdateEmailVo updateEmailVo, HttpServletRequest request){
        String username = jwtAuthService.getTokenUsername(request);
        return userService.updateUserEmailByEmailCode(username,updateEmailVo);
    }

    /*
    修改电话 已测试
     */
    @ApiModelProperty("修改电话")
    @PutMapping("/updatePhone")
    public RestResult updateUserPhone(@Valid @RequestBody PhoneVo phoneVo,HttpServletRequest request){
        String username = jwtAuthService.getTokenUsername(request);

        return userService.updateUserPhoneByUsername(username,phoneVo.getPhone());

    }
}
