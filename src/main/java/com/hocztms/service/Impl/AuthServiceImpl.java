package com.hocztms.service.Impl;

import com.hocztms.common.RestResult;
import com.hocztms.entity.Email;
import com.hocztms.entity.Users;
import com.hocztms.redis.RedisService;
import com.hocztms.service.AuthService;
import com.hocztms.service.UserService;
import com.hocztms.springSecurity.jwt.JwtAuthService;
import com.hocztms.utils.EamilUtils;
import com.hocztms.webSocket.WebSocketServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;




@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private RedisService redisService;
    @Autowired
    private JwtAuthService jwtAuthService;
    @Autowired
    private WebSocketServer webSocketServer;
    @Autowired
    private UserService userService;
    @Autowired
    private EamilUtils eamilUtils;
    @Autowired
    private PasswordEncoder passwordEncoder;



    @Override
    public RestResult authLogout(HttpServletRequest request) {
        try {

            String username = jwtAuthService.getTokenUsername(request);

            //主动失效 设置黑名单 并关闭已存在socket
            redisService.userLogoutByServer(username);
            return new RestResult(1,"操作成功",null);
        }catch (Exception e){
            return null;
        }
    }

    @Override
    public RestResult authLogin(String username, String password) {
        try {
            Users users = userService.findUsersByUsername(username);
            if (users==null){
                return new RestResult(0,"用户不存在",null);
            }

            if(users.getStatus()==0){
                return new RestResult(0,"账户已冻结",null);
            }

            if (!passwordEncoder.matches(password,users.getPassword())){
                redisService.setUserLoginLimit(username);
                return new RestResult(0,"密码错误",null);
            }

            return jwtAuthService.login(username,password);
        }catch (Exception e){
            return new RestResult(0,e.getMessage(),null);
        }
    }


    @Override
    public RestResult authRegister(Users users) {
        try {
            Users usersByUsername = userService.findUsersByUsername(users.getUsername());
            Users usersByEmail = userService.findUsersByEmail(users.getEmail());
            Users usersByPhone = userService.findUsersByPhone(users.getPhone());
            if (usersByUsername != null) {
                return new RestResult(0, "用户名已存在", null);
            }
            if (usersByEmail!=null) {
                return new RestResult(0, "该邮箱已注册", null);
            }

            if (usersByPhone!=null) {
                return new RestResult(0, "该手机号已注册", null);
            }

            //发送注册成功邮件
            Email email = new Email(users.getUsername(),users.getEmail(),"通知","您的账号 " + users.getUsername()+" 注册成功",new Date(),null);
            eamilUtils.sendEmail(email);


            userService.insertUser(users);

            return new RestResult(1, "注册成功", null);
        } catch (Exception e) {
            return new RestResult(0, e.getMessage(), null);
        }
    }



}
