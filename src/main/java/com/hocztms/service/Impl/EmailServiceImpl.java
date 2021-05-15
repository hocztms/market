package com.hocztms.service.Impl;

import com.hocztms.common.RestResult;
import com.hocztms.entity.Email;
import com.hocztms.entity.Goods;
import com.hocztms.entity.Users;
import com.hocztms.redis.RedisService;
import com.hocztms.service.EmailService;
import com.hocztms.service.GoodsService;
import com.hocztms.service.UserService;
import com.hocztms.utils.CodeUtils;
import com.hocztms.utils.EamilUtils;
import com.hocztms.utils.ResultUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.Date;
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
    private RedisTemplate<String, String> codeRedisTemplate;

    @Autowired
    private RedisService redisService;



    private static final long outTime  = 10*60*1000;//设置30分钟有效



    @Override
    public RestResult sendPasswordEmail(String email) {
        try {
            Users users = userService.findUsersByEmail(email);

            if (users==null){
                return ResultUtils.error(0,"账号不存在");
            }

            if (!redisService.checkUserRePasswordLimit(users.getUsername())){
                return ResultUtils.error(0,"已达上线 请24小时后重新获取");
            }
            if (codeRedisTemplate.opsForValue().get("re&"+users.getUsername())!=null){
                return ResultUtils.error(0,"已发送,请勿重新获取");
            }

            String secret = String.valueOf(UUID.randomUUID());

            log.info(users.getUsername() + ":本次密钥为" + secret);
            Date date = new Date(new Date().getTime() + outTime);
            Email sendEmail = new Email(users.getUsername(), email, "Get  Password", null, date, secret);

            codeRedisTemplate.opsForValue().set("re&"+users.getUsername(),secret,10, TimeUnit.MINUTES);

            System.out.println("re&"+users.getUsername());
            redisService.setUserRePasswordLimit(users.getUsername());

            eamilUtils.sendGetPasswordEamil(sendEmail);

            return ResultUtils.success();
        }catch (Exception e){
            return ResultUtils.error(-1,"error");
        }
    }

    @Override
    public void sendCheckGoodsEmail(Long goodsId, int tag) {
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
        eamilUtils.sendEmail(email);
    }




    @Override
    public RestResult sendRegisterEmailCode(String email, HttpSession httpSession) {
        try {

            Users users = userService.findUsersByEmail(email);

            if (users!=null){
                log.warn(email + "已经被注册.....");
                return ResultUtils.error(0,"邮箱已被注册");
            }

            if (codeRedisTemplate.opsForValue().get(RedisService.registerPrefix+email)!=null){
                return new RestResult(0,"请勿重复获取",null);
            }

            //生成随机数 //同时 存入redis
            String code = codeUtils.generateCode(RedisService.registerPrefix+email,1);


            log.info("sendRegisterEmailCode..."+email + "   "+ code);


            Email sendEmail = new Email(null,email,"Register",null,new Date(),code);
            eamilUtils.sendCodeEmail(sendEmail);
            return ResultUtils.success();
        }catch (Exception e){
            return ResultUtils.error(-1,"error");
        }

    }

    @Override
    public RestResult getUpdateEmailCode(String username) {
        try {
            if (codeRedisTemplate.opsForValue().get(RedisService.updateEmailPrefix+username)!=null){
                return ResultUtils.error(0,"请勿重复获取");
            }

            String code = codeUtils.generateCode(RedisService.updateEmailPrefix + username,5);
            Users user = userService.findUsersByUsername(username);

            Email email  = new Email(username,user.getEmail(),"updateEmail","您本次修改邮箱验证码为" + code + "5分钟有效",new Date(),code);
            eamilUtils.sendEmail(email);

            return ResultUtils.success();
        }catch (Exception e){
            return ResultUtils.error(-1,"error");
        }
    }


}
