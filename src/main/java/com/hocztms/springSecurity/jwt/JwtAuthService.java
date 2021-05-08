package com.hocztms.springSecurity.jwt;

import com.hocztms.common.RestResult;
import com.hocztms.service.UserService;
import com.hocztms.springSecurity.entity.MyUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

@Service
public class JwtAuthService  {

    // 此处注入的bean在SpringConfig中产生, 如果不在其中声明则注入AuthenticationManager报错
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenUtils jwtTokenUtils;

    @Autowired
    private UserService userService;


    public RestResult login(String username, String password) {
        Authentication authentication = null;
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
        String authorties = String.valueOf(loginUser.getAuthorities());
        if (authorties.contains("admin")){
            System.out.println("管理员 "+ loginUser.getUsername() + " 已登录");
            result.setCode(2);
        }
        else {
            System.out.println("用户 "+ loginUser.getUsername() + " 已登录");
        }
        // 生成token
        userService.updateUserLastLoginDate(username);
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
