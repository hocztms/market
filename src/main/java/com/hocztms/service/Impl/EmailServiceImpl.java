package com.hocztms.service.Impl;

import com.hocztms.common.RestResult;
import com.hocztms.entity.Email;
import com.hocztms.entity.Goods;
import com.hocztms.entity.Users;
import com.hocztms.service.EmailService;
import com.hocztms.service.GoodsService;
import com.hocztms.service.UserService;
import com.hocztms.utils.CodeUtils;
import com.hocztms.utils.EamilUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class EmailServiceImpl implements EmailService {

    @Autowired
    private EamilUtils eamilUtils;

    @Autowired
    private UserService userService;

    @Autowired
    private GoodsService goodsService;

    @Autowired
    private CodeUtils codeUtils;

    @Resource
    private RedisTemplate<String, String> redisTemplate;



    private static final long outTime  = 10*60*1000;//设置30分钟有效



    @Override
    public RestResult sendPasswordEmail(String username, String emailAddress) {
        try {
            Users users = userService.findUsersByUsername(username);

            if (redisTemplate.opsForValue().get("re&"+username)!=null){
                return new RestResult(0,"已发送,请勿重新获取",null);
            }
            if (users==null){
                return new RestResult(0,"账号不存在",null);
            }

            if (!users.getEmail().equals(emailAddress)){
                return new RestResult(0,"邮箱不正确",null);
            }


            String secret = String.valueOf(UUID.randomUUID());

            log.info(username + ":本次密钥为" + secret);
            Date date = new Date(new Date().getTime() + outTime);
            Email email = new Email(username, emailAddress, "Get  Password", null, date, secret);

            redisTemplate.opsForValue().set("re&"+username,secret,10, TimeUnit.MINUTES);

            eamilUtils.sendGetPasswordEamil(email);
        }catch (Exception e){
            return new RestResult(0,e.getMessage(),null);
        }
        return new RestResult(1,"发送成功",null);
    }

    @Override
    public void sendCheckGoodsEmail(Long goodsId, int tag) {
        try {
            Goods goods = goodsService.findGoodsByGoodsId(goodsId);
            Users users = userService.findUsersByUsername(goods.getSeller());
            String msg;
            if (tag==1){
                msg = "您的商品:  " + goods.getMsg() + "   商品ID:  " + goods.getId() + "  审核通过";
            }
            else {
                msg = "您的商品:  " + goods.getMsg() + "   商品ID:  " + goods.getId() + "  审核不通过";
            }
            Email email = new Email(users.getUsername(),users.getEmail(),"通知", msg, new Date(), null);
            eamilUtils.sendEamil(email);
        }catch (Exception e){
            throw new RuntimeException(e.getMessage());
        }
    }




    @Override
    public RestResult sendRegisterEmailCode(String email, HttpSession httpSession) {
        try {
            if (redisTemplate.opsForValue().get("register$"+email)!=null){
                return new RestResult(0,"请勿重复获取",null);
            }

            //生成随机数 //同时 存入redis
            String code = codeUtils.generateCode("register$"+email,1);


            List<Users> users = userService.findUsersByEmail(email);

            if (!users.isEmpty()){
                log.warn(email + "已经被注册.....");
                return new RestResult(0,"邮箱已被注册",null);
            }

            log.info("sendRegisterEmailCode..."+email + "   "+ code);


            Email sendEmail = new Email(null,email,"Register",null,new Date(),code);
            eamilUtils.sendCodeEmail(sendEmail);
            return new RestResult(1,"验证码已发送",null);
        }catch (Exception e){
            return new RestResult(0,"发送失败",null);
        }

    }

    @Override
    public RestResult getUpdateEmailCode(String username) {
        try {
            if (redisTemplate.opsForValue().get("updateEmail:"+username)!=null){
                return new RestResult(0,"请勿重复获取",null);
            }

            String code = codeUtils.generateCode("updateEmail&" + username,5);
            Users user = userService.findUsersByUsername(username);

            Email email  = new Email(username,user.getEmail(),"updateEmail","您本次修改邮箱验证码为" + code + "5分钟有效",new Date(),code);
            eamilUtils.sendEamil(email);

            return new RestResult(1,"操作成功",null);
        }catch (Exception e){
            return new RestResult(0,"失败",null);
        }
    }


}