package com.hocztms.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hocztms.common.RestResult;
import com.hocztms.entity.Address;
import com.hocztms.entity.Users;
import com.hocztms.mapper.AddressMapper;
import com.hocztms.service.AddressService;
import com.hocztms.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AddressServiceImpl implements AddressService {

    @Autowired
    private AddressMapper addressMapper;

    @Autowired
    private UserService userService;
    @Override
    public RestResult getUserAddress(String username) {
        try {
            List<Address> addresses = findAddressByUsername(username);
            return new RestResult(1,"成功",addresses);
        }catch (Exception e){
            return new RestResult(0,"系统错误 联系管理员",null);
        }
    }

    @Override
    public RestResult createAddress(Address address,String username) {
        try {
            Users users = userService.findUsersByUsername(username);
            if (users==null){
                return new RestResult(0,"违法操作,用户名不存在",null);
            }

            if (!address.getUsername().equals(username)){
                return new RestResult(0,"违法操作,无权限",null);
            }

            address.setUsername(username);
            addressMapper.insert(address);
            return new RestResult(1,"成功",null);
        }catch (Exception e){
            System.out.println(e.getMessage());
            return new RestResult(0,"系统错误 联系管理员",null);
        }
    }

    @Override
    public RestResult deleteUserAddressByAddressIds(List<Long> ids, String username) {
        try {

            //非法操作判断
            for (Long id:ids){
                Address address = findAddressById(id);
                if (address==null){
                    return new RestResult(0,id+"  地址不存在 非法操作",null);
                }
                if (!address.getUsername().equals(username)){
                    return new RestResult(0,id+"  无权限 非法操作",null);
                }
            }

            RestResult result = new RestResult(1,"成功",null);

            for (Long id:ids){
                if (deleteUserAddressById(id)==0){
                    result.put("error",id + "删除失败");
                }
            }

            return result;
        }catch (Exception e){
            return new RestResult(0,"失败",null);
        }
    }

    @Override
    public RestResult updateUserAddress(Address address, String username) {
        try {
            if(!address.getUsername().equals(username)){
                return new RestResult(0,"无权限",null);
            }

            if(updateUserAddress(address)==0){
                return new RestResult(0,"更改失败",null);
            }
            return new RestResult(1,"成功",null);
        }catch (Exception e){
            return new RestResult(0,"系统错误 联系管理员",null);
        }
    }

    @Override
    public List<Address> findAddressByUsername(String username) {
        QueryWrapper<Address> wrapper = new QueryWrapper<>();
        wrapper.eq("username",username);
        return addressMapper.selectList(wrapper);
    }

    @Override
    public Integer deleteUserAddressById(Long id) {
        return addressMapper.deleteById(id);
    }

    @Override
    public Integer updateUserAddress(Address address) {
        return addressMapper.updateById(address);
    }

    @Override
    public boolean isEmpty(Address address) {
        return address.getUsername() == null || address.getAddress() == null || address.getPhone() == null || address.getReceiver() == null;
    }

    @Override
    public Address findAddressById(Long id) {
        return addressMapper.selectById(id);
    }
}
