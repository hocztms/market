package com.hocztms.springSecurity.jwt;

import com.hocztms.redis.RedisService;
import io.jsonwebtoken.*;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Data
@Component
@Slf4j
public class JwtTokenUtils {

    //密钥
    private String secret = "hocztms";

    private int expiration = 3600000;

    private String header = "token";


    private static Key KEY = null;

    @Autowired
    private RedisService redisService;


    //生成token
    public String generateToken(UserDetails userDetails) {
        log.info("generateToken执行了..." + userDetails.toString());
        Map<String, Object> claims = new HashMap<>(2);
        claims.put("sub", userDetails.getUsername());
        claims.put("created", new Date());


        return generateToken(claims);
    }


   //解析token
    public String getUsernameFromToken(String token) {
        String username;
        try {


            Claims claims = getClaimsFromToken(token);
            username = claims.get("sub",String.class);
        } catch (Exception e) {
            username = null;
        }
        return username;
    }

   //判断token有效时间
    public Boolean isTokenExpired(String token) {
        try {
            Claims claims = getClaimsFromToken(token);
            Date expiration = claims.getExpiration();
            return expiration.before(new Date());
        } catch (Exception e) {
            return false;
        }
    }

    //刷新token
    public String refreshToken(String token) {
        String refreshedToken;
        try {
            Claims claims = getClaimsFromToken(token);
            claims.put("created", new Date());


            refreshedToken = generateToken(claims);
        } catch (Exception e) {
            refreshedToken = null;
        }
        return refreshedToken;
    }

    //刷新token
    public Date getDateFromToken(String token) {

        try {
            Claims claims = getClaimsFromToken(token);
            return claims.get("created", Date.class);
        } catch (Exception e) {
            throw new RuntimeException("token失效");
        }
    }


    //验证token
    public Boolean validateToken(String token, UserDetails userDetails) {

        String username = getUsernameFromToken(token);


        return (username.equals(userDetails.getUsername()) &&
                !isTokenExpired(token) && checkRedisBlack(token));
    }

    public Boolean checkRedisBlack(String token){
        String username = getUsernameFromToken(token);
        if (username==null){
            return false;
        }

        Date date = redisService.getUserBlackDate(username);

        Date jwtDate = getDateFromToken(token);

        return date == null || !jwtDate.before(date);
    }


    //生成token
    private String generateToken(Map<String, Object> claims) {
        Date expirationDate = new Date(System.currentTimeMillis() + expiration);
        return Jwts.builder().setClaims(claims)
                .setExpiration(expirationDate)
                .signWith(SignatureAlgorithm.HS256, getKeyInstance())
                .compact();
    }

    //解析token 获得claim
    private Claims getClaimsFromToken(String token) {
        Claims claims;

        try {
            claims = Jwts.parser().setSigningKey(getKeyInstance()).parseClaimsJws(token).getBody();
//            claims = Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
        } catch (Exception e) {
            claims = null;
        }
        return claims;
    }


    private Key getKeyInstance() {
        if (KEY == null) {
            synchronized (JwtTokenUtils.class) {
                if (KEY == null) {// 双重锁
                    byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(secret);
                    KEY = new SecretKeySpec(apiKeySecretBytes, SignatureAlgorithm.HS256.getJcaName());
                }
            }
        }
        return KEY;
    }
}