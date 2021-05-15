package com.hocztms.service.Impl;

import com.hocztms.common.RestResult;
import com.hocztms.entity.Email;
import com.hocztms.entity.Users;
import com.hocztms.redis.RedisService;
import com.hocztms.service.AuthService;
import com.hocztms.service.UserService;
import com.hocztms.springSecurity.jwt.JwtAuthService;
import com.hocztms.utils.EamilUtils;
import com.hocztms.utils.ResultUtils;
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
            return ResultUtils.success();
        }catch (Exception e){
            return ResultUtils.error(-1,"error");
        }
    }

    @Override
    public RestResult authLogin(String username, String password) {
        try {
            Users users = userService.findUsersByUsername(username);
            if (users==null){
                return ResultUtils.error(0,"用户不存在");
            }

            if(users.getStatus()==0){
                return ResultUtils.error(0,"账户已冻结");
            }

            if (!passwordEncoder.matches(password,users.getPassword())){
                redisService.setUserLoginLimit(username);
                return ResultUtils.error(0,"密码错误");
            }

            return jwtAuthService.login(username,password);
        }catch (Exception e){
            return ResultUtils.error(-1,"error");
        }
    }


    @Override
    public RestResult authRegister(Users users) {
        try {
            Users usersByUsername = userService.findUsersByUsername(users.getUsername());
            Users usersByEmail = userService.findUsersByEmail(users.getEmail());
            Users usersByPhone = userService.findUsersByPhone(users.getPhone());
            if (usersByUsername != null) {
                return ResultUtils.error(0, "用户名已存在");
            }
            if (usersByEmail!=null) {
                return ResultUtils.error(0, "该邮箱已注册");
            }

            if (usersByPhone!=null) {
                return ResultUtils.error(0, "该手机号已注册");
            }

            //发送注册成功邮件
            Email email = new Email(users.getUsername(),users.getEmail(),"通知","您的账号 " + users.getUsername()+" 注册成功",new Date(),null);
            eamilUtils.sendEmail(email);


            userService.insertUser(users);

            return ResultUtils.success();
        } catch (Exception e) {
            return ResultUtils.error(-1,"error");
        }
    }



}
