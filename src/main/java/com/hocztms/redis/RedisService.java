package com.hocztms.redis;

import com.hocztms.webSocket.WebSocketServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.concurrent.TimeUnit;

@Service
public class RedisService {


    public static String jwtPrefix = "JWTWeb&";

    public static String loginLimitPrefix = "loginLimit&";

    public static String rePasswordLimitPrefix = "rePasswordLimit&";

    public static String registerPrefix = "register$";

    public static String loginPrefix = "login$";

    public static String updateEmailPrefix = "updateEmail&";


    @Autowired
    private RedisTemplate<String, Date> jwtRedisTemplate;

    @Autowired
    private RedisTemplate<String,Long> limitRedisTemplate;

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

    public void setUserLoginLimit(String username){
        Long limit = limitRedisTemplate.opsForValue().get(loginLimitPrefix+username);
        if (limit!=null){
            limitRedisTemplate.opsForValue().set(loginLimitPrefix+username,limit+1,15,TimeUnit.MINUTES);
            return;
        }
        limitRedisTemplate.opsForValue().set(loginLimitPrefix+username,Long.parseLong("1"),15,TimeUnit.MINUTES);
    }

    public void setUserRePasswordLimit(String username){
        Long limit = limitRedisTemplate.opsForValue().get(rePasswordLimitPrefix+username);
        if (limit!=null){
            limitRedisTemplate.opsForValue().set(rePasswordLimitPrefix+username,limit+1,1,TimeUnit.DAYS);
            return;
        }
        limitRedisTemplate.opsForValue().set(rePasswordLimitPrefix+username,Long.parseLong("1"),1,TimeUnit.DAYS);
    }

    public boolean checkUserLoginLimit(String username){
        Long limit = limitRedisTemplate.opsForValue().get(loginLimitPrefix+username);
        System.out.println(limit);
        if (limit==null||limit<5){
            return true;
        }
        return false;
    }

    public boolean checkUserRePasswordLimit(String username){
        Long limit = limitRedisTemplate.opsForValue().get(rePasswordLimitPrefix+username);
        if (limit==null||limit<3){
            return true;
        }
        return false;
    }
}
