package com.hocztms.redis;

import com.hocztms.utils.RedisUtils;
import com.hocztms.webSocket.WebSocketServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.concurrent.TimeUnit;

@Service
public class RedisService {


    public static String jwtPrefix = "JWTWeb&";

    @Autowired
    private RedisTemplate<String, Date> jwtRedisTemplate;

    @Autowired
    private WebSocketServer webSocketServer;

    public Integer userLogoutByServer(String username){
        try {
            jwtRedisTemplate.opsForValue().set(jwtPrefix +username,new Date(),60, TimeUnit.MINUTES);
            System.out.println("ok");
            webSocketServer.close(username);
            return 1;
        }catch (Exception e){
            return 0;
        }
    }

    public Date getUserBlackDate(String username){
        return jwtRedisTemplate.opsForValue().get(jwtPrefix+username);
    }
}
