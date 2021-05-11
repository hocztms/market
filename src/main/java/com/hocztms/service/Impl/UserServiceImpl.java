package com.hocztms.service.Impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hocztms.common.RestResult;
import com.hocztms.entity.*;
import com.hocztms.mapper.*;
import com.hocztms.redis.RedisService;
import com.hocztms.service.*;
import com.hocztms.utils.EamilUtils;
import com.hocztms.vo.PasswordEmail;
import com.hocztms.vo.UpdateEmailVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UsersMapper usersMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RoleMapper roleMapper;

    @Autowired
    private EamilUtils eamilUtils;

    @Autowired
    private GoodsService goodsService;

    @Autowired
    private IllegalMapper illegalMapper;

    @Autowired
    private IllegalUserService illegalUserService;

    @Resource
    private RedisTemplate<String, String> codeRedisTemplate;

    @Autowired
    private RedisService redisService;




    @Override
    public RestResult ReUserPasswordBySecret(PasswordEmail passwordEmail) {
        try{

            String keyValue = codeRedisTemplate.opsForValue().get("re&" + passwordEmail.getUsername());

            if (keyValue==null){
                return new RestResult(0,"请重新获取密钥",null);
            }
            if (!keyValue.equals(passwordEmail.getSecret())){
                return new RestResult(0,"密钥不正确请重新输入",null);
            }

            Users users = findUsersByUsername(passwordEmail.getUsername());

            users.setPassword(passwordEncoder.encode(passwordEmail.getPassword()));

            if (updateUser(users)==0){
                return new RestResult(0,"修改失败",null);
            }

            //删除值
            codeRedisTemplate.delete("re&"+users.getUsername());


            //主动失效 设置黑名单 并关闭已存在socket
            redisService.userLogoutByServer(users.getUsername());
            return new RestResult(1,"修改成功",null);
        }catch (Exception e){
            return new RestResult(0,e.getMessage(),null);
        }
    }

    @Override
    public Integer insertUser(Users users) {
        try {
            Role role = new Role(users.getUsername(),"user");
            Illegal illegal = illegalUserService.findIllegalUserByUsername(users.getUsername());
            if (illegal!=null){
                illegalUserService.deleteIllegalUserByUsername(users.getUsername());
            }

            String encodePassword = passwordEncoder.encode(users.getPassword());
            users.setPassword(encodePassword);
            users.setStatus(1);
            usersMapper.insert(users);
            roleMapper.insert(role);
            illegalMapper.insert(new Illegal(users.getUsername(),0,1,1));
            return 1;
        } catch (Exception e) {
            return 0;
        }
    }

    @Override
    public RestResult updateUserEmailByEmailCode(String username, UpdateEmailVo updateEmailVo) {
        try {
            String redisCode = codeRedisTemplate.opsForValue().get("updateEmail$"+username);
            if (redisCode==null){
                return new RestResult(0,"请先获取验证码",null);
            }

            if (findUsersByEmail(updateEmailVo.getEmail())!=null){
                return new RestResult(0,"邮箱已注册",null);
            }
            Users users = findUsersByUsername(username);

            users.setEmail(updateEmailVo.getEmail());
            updateUser(users);
            return new RestResult(1,"操作成功",null);
        }catch (Exception e){
            return new RestResult(0,"操作失败",null);
        }
    }

    @Override
    public RestResult updateUserPhoneByUsername(String username, String phone) {
        try {
            if (findUsersByPhone(phone)!=null){
                return new RestResult(0,"该手机已注册",null);
            }
            Users usersByUsername = findUsersByUsername(username);
            usersByUsername.setPhone(phone);
            updateUser(usersByUsername);
            return new RestResult(1,"操作成功",null);
        }catch (Exception e){
            return new RestResult(0,"操作失败",null);
        }
    }

    @Override
    public List<Users> findUsersByEmail(String email) {
        QueryWrapper<Users> wrapper = new QueryWrapper<>();
        wrapper.eq("email",email);
        return usersMapper.selectList(wrapper);

    }

    @Override
    public List<Users> findUsersByPhone(String phone) {
        QueryWrapper<Users> wrapper = new QueryWrapper<>();
        wrapper.eq("phone",phone);
        return usersMapper.selectList(wrapper);
    }

    @Override
    public List<Role> getUserRoles(String username) {
        QueryWrapper<Role> wrapper = new QueryWrapper<>();
        wrapper.eq("username",username);
        return roleMapper.selectList(wrapper);
    }

    @Override
    public RestResult getUserGoods(String username) {
        try {
            List<Goods> goods = goodsService.selectListByUsername(username);
            return new RestResult(1,"成功",goods);
        }catch (Exception e){
            return new RestResult(1,"失败",0);
        }
    }

    @Override
    public Integer updateUser(Users users) {
        QueryWrapper<Users> wrapper = new QueryWrapper<>();
        wrapper.eq("username",users.getUsername());
        return usersMapper.update(users,wrapper);
    }

    @Override
    public Integer updateUserStatusByUsername(String username,int status) {
        if (illegalUserService.updateIllegalUserStatusByUsername(username,status)==0) {
            return 0;
        }
        QueryWrapper <Users> wrapper = new QueryWrapper<>();
        wrapper.eq("username",username);
        Users usersByUsername = findUsersByUsername(username);
        usersByUsername.setStatus(status);
        return usersMapper.update(usersByUsername,wrapper);
    }

    @Override
    public String userContactToStringByUsername(String username) {
        Users user = findUsersByUsername(username);
        return "email:" + user.getEmail() + "  phone:" + user.getPhone();
    }



    @Override
    public Users findUsersByUsername(String username) {
        QueryWrapper<Users> wrapper = new QueryWrapper<>();
        wrapper.eq("username",username);
        return usersMapper.selectOne(wrapper);
    }


}
