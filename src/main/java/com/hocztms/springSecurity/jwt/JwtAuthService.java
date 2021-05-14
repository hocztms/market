package com.hocztms.springSecurity.jwt;

import com.hocztms.common.RestResult;
import com.hocztms.redis.RedisService;
import com.hocztms.service.UserService;
import com.hocztms.springSecurity.entity.MyUserDetails;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

@Service
@Slf4j
public class JwtAuthService  {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenUtils jwtTokenUtils;

    @Autowired
    private UserService userService;

    @Autowired
    private RedisService redisService;


    public RestResult login(String username, String password) {
        Authentication authentication;
        try {
            // 进行身份验证,
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password));
        } catch (Exception e) {
            return new RestResult(0,e.getMessage(),null);
        }

        MyUserDetails loginUser = (MyUserDetails) authentication.getPrincipal();


        RestResult result = new RestResult(1,"登入成功",null);

        //判断是否为管理员
        String authorities = String.valueOf(loginUser.getAuthorities());

        //主动失效 设置黑名单 并关闭已存在socket
        if (redisService.userLogoutByServer(username)==0){
            return null;
        }

        if (authorities.contains("admin")){
            log.info("管理员 "+ loginUser.getUsername() + " 已登录");
            result.setCode(2);
        }

        else {
            log.info("用户 "+ loginUser.getUsername() + " 已登录");
        }

        result.put("token",jwtTokenUtils.generateToken(loginUser));
        return result;
    }

    public String getToken(HttpServletRequest request){
        return request.getHeader(jwtTokenUtils.getHeader());
    }

    public String getTokenUsername(HttpServletRequest request){
        String token = request.getHeader(jwtTokenUtils.getHeader());
        return jwtTokenUtils.getUsernameFromToken(token);
    }

}
