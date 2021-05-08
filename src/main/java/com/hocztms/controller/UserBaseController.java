package com.hocztms.controller;


import com.hocztms.common.RestResult;
import com.hocztms.vo.AuthVo;
import com.hocztms.vo.PasswordEmail;
import com.hocztms.entity.Users;
import com.hocztms.service.EmailService;
import com.hocztms.service.UserService;
import com.hocztms.springSecurity.jwt.JwtAuthService;
import com.hocztms.utils.CodeUtils;
import com.hocztms.vo.EmailVo;
import com.hocztms.vo.UserPasswordVo;
import com.hocztms.vo.UserVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Date;

/*
用户系统主要接口
 */
@PreAuthorize("permitAll()")
@RestController
@RequestMapping("/auth")
@Api(tags = "用户系统基础方法说明，权限要求：无")
@Slf4j
public class UserBaseController {


    @Autowired
    JwtAuthService jwtAuthService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private UserService userService;

    @Autowired
    private CodeUtils codeUtils;

    /*
    用户登录 (管理员和用户都包含 返回RestResult中 code = 1为用户 2为管理员 0为登录失败)
     */
    @ApiOperation("用户登录 (管理员和用户都包含 返回RestResult中 code = 1为用户 2为管理员 0为登录失败)")
    @PostMapping("/login")
    public RestResult userLogin (
            @Valid @RequestBody AuthVo authVo) {
        if (!codeUtils.checkKeyValueByKey(authVo.getUsername(),authVo.getCode())){
            return new RestResult(0,"验证码不正确",null);
        }
        else {
            return jwtAuthService.login(authVo.getUsername(),authVo.getPassword());
        }
    }

    /*
    用户注册
     */
    @ApiOperation("用户注册")
    @PostMapping("/register")
    public RestResult register(
            @Valid @RequestBody UserVo userVo)
    {
        if (!codeUtils.checkKeyValueByKey("register$"+userVo.getEmail(),userVo.getCode())){
            return new RestResult(0,"验证码不正确",null);
        }
        return userService.userRegister(new Users(userVo.getUsername(),userVo.getPassword(),userVo.getEmail(),userVo.getPhone(),1,new Date()));
    }

    /*
    用户注销
     */
    @ApiOperation("用户注销,需要前端删除掉token后调用的方法 此方法只单纯验证 请求头中是否还含有token")
    @PostMapping("/logout")
    public RestResult logout(HttpServletRequest request) {

        String username = jwtAuthService.getTokenUsername(request);

        //注销 更改最后登录时间
        userService.updateUserLastLoginDate(username);
        return new RestResult(1,"操作成功",null);

    }

    /*
    发送邮箱注册验证码
     */
    @ApiOperation("发送邮箱注册验证码")
    @PostMapping("/sendEmailCode")
    public RestResult register(@Valid @RequestBody EmailVo email, HttpServletRequest request) {
        return emailService.sendRegisterEmailCode(email.getEmail(),request.getSession());
    }


    /*
    用户找回密码
     */
    @ApiOperation("用户找回密码 传username 和 email")
    @PostMapping("/getPassword")
    public RestResult getPassword(
            @Valid @RequestBody UserPasswordVo users){
        return emailService.sendPasswordEmail(users.getUsername(),users.getEmail());
    }

    /*
    用户根据密钥修改密码
     */
    @ApiOperation("用户根据密钥修改密码 传username,password,secret")
    @PostMapping("/rePassword")
    public RestResult rePassword(@Valid @RequestBody PasswordEmail passwordEmail){
        return userService.ReUserPasswordBySecret(passwordEmail);
    }

    /*
    用户根据获取验证码
     */
    @ApiOperation("用户根据获取验证码")
    @GetMapping("/getCode")
    public void getCode(
            String username,
            HttpServletResponse response) throws IOException {
        BufferedImage bi = codeUtils.getLoginImgCode(username);
        ImageIO.write(bi,"JPG",response.getOutputStream());
    }


}
