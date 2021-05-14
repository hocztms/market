package com.hocztms.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class CodeUtils {

    @Autowired
    private RedisTemplate<String, String> codeRedisTemplate;

    public static char[] ch = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".toCharArray();

    public  BufferedImage getLoginImgCode(String key){
        BufferedImage bufferedImage = new BufferedImage(70,25,BufferedImage.TYPE_INT_RGB);
        Graphics graphics = bufferedImage.getGraphics();
        Color color = new Color(249,205,173);
        graphics.setColor(color);
        graphics.fillRect(0,0,440,40);

        Random random = new Random();
        int len = ch.length;
        int index;
        StringBuffer stringBuffer = new StringBuffer();

        for (int i=0;i<4;i++){
            index = random.nextInt(len);
            graphics.setColor(new Color(random.nextInt(200),random.nextInt(200),random.nextInt(200)));
            graphics.drawString(ch[index]+"",(i*15)+3,18);//画随机字符
            stringBuffer.append(ch[index]);
        }

        if (codeRedisTemplate.opsForValue().get(key)!=null){
            codeRedisTemplate.delete(key);
        }

        //存入redis 1分钟有效
        codeRedisTemplate.opsForValue().set(key,stringBuffer.toString(),1, TimeUnit.MINUTES);
        log.info(key + "本次密钥为" + stringBuffer.toString());
        return bufferedImage;
//        ImageIO.write(bi,"JPG",response.getOutputStream());
    }


    public  boolean codeIsEmpty(String key){
        String code = codeRedisTemplate.opsForValue().get(key);
        if (code==null){
            return true;
        }
        return false;
    }

    public  boolean checkKeyValueByKey(String key,String value){
        String code = codeRedisTemplate.opsForValue().get(key);
        if (code==null){
            return false;
        }
        if (!code.equals(value)){
            return false;
        }
        codeRedisTemplate.delete(key);
        return true;
    }

    public  String generateCode(String key,int minutes){
        int len = ch.length;
        int index;
        Random random = new Random();
        StringBuffer stringBuffer = new StringBuffer();
        for (int i = 0;i<4;i++){
            index = random.nextInt(len);
            stringBuffer.append(ch[index]);
        }
        if (codeRedisTemplate.opsForValue().get(key)!=null){
            codeRedisTemplate.delete(key);
        }

        //存入redis 1分钟有效
        codeRedisTemplate.opsForValue().set(key,stringBuffer.toString(),minutes,TimeUnit.MINUTES);
        return stringBuffer.toString();
    }


}
