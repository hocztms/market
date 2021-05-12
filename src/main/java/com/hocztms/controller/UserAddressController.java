package com.hocztms.controller;

import com.hocztms.common.RestResult;
import com.hocztms.entity.Address;
import com.hocztms.service.AddressService;
import com.hocztms.springSecurity.jwt.JwtAuthService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Api(tags = "用户地址相关功能方法说明，权限要求：用户")
@PreAuthorize("hasAuthority('user')")

@RestController
@RequestMapping("/users")
public class UserAddressController {

    @Autowired
    private JwtAuthService jwtAuthService;

    @Autowired
    private AddressService addressService;


    /*
    获取用户地址信息
     */
    @ApiOperation("获取用户地址信息")
    @GetMapping("/getAddress")
    public RestResult getUserAddress(HttpServletRequest request) {
        String tokenUsername = jwtAuthService.getTokenUsername(request);
        return addressService.getUserAddress(tokenUsername);
    }

    /*
    创建用户地址
     */
    @ApiOperation("创建用户地址")
    @PostMapping("/createAddress")
    public RestResult createAddress(@RequestBody Address address, HttpServletRequest request) {
        if (addressService.isEmpty(address)) {
            return new RestResult(0, "数据不能为空", null);
        }
        String username = jwtAuthService.getTokenUsername(request);
        return addressService.createAddress(address, username);
    }

    /*
    删除用户地址
     */
    @ApiOperation("删除用户地址")
    @DeleteMapping("/deleteAddress")
    public RestResult deleteAddress(@RequestBody List<Long> ids, HttpServletRequest request){
        if (ids.isEmpty()){
            return new RestResult(0,"数据不能为空",null);
        }
        String username = jwtAuthService.getTokenUsername(request);
        return addressService.deleteUserAddressByAddressIds(ids,username);
    }

    /*
    更新用户地址
     */
    @ApiOperation("更新用户地址")
    @PutMapping("/updateAddress")
    public RestResult updateAddress(@RequestBody Address address, HttpServletRequest request) {
        if (addressService.isEmpty(address)) {
            return new RestResult(0, "数据不能为空", null);
        }
        String username = jwtAuthService.getTokenUsername(request);
        return addressService.updateUserAddress(address,username);
    }

}
