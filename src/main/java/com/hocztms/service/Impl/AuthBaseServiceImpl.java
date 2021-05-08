package com.hocztms.service.Impl;

import com.hocztms.common.RestResult;
import com.hocztms.service.AuthBaseService;
import com.hocztms.springSecurity.jwt.JwtAuthService;
import com.hocztms.springSecurity.jwt.JwtTokenUtils;
import com.hocztms.utils.RedisUtils;
import com.hocztms.webSocket.WebSocketServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@Service
public class AuthBaseServiceImpl implements AuthBaseService {

    @Autowired
    private RedisTemplate<String, Date> jwtRedisTemplate;
    @Autowired
    private JwtAuthService jwtAuthService;
    @Autowired
    private WebSocketServer webSocketServer;


    @Override
    public RestResult authLogout(HttpServletRequest request) {
        try {

            String username = jwtAuthService.getTokenUsername(request);
            jwtRedisTemplate.opsForValue().set(RedisUtils.jwtPrefix +username,new Date(),60, TimeUnit.MINUTES);
            webSocketServer.close(username);
            return new RestResult(1,"操作成功",null);
        }catch (Exception e){
            return null;
        }
    }
}
