package com.hocztms.controller;

import com.hocztms.common.RestResult;
import com.hocztms.service.OrderFormService;
import com.hocztms.springSecurity.jwt.JwtAuthService;
import com.hocztms.vo.OrderFormVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;
@Api(tags = "用户订单相关功能方法说明，权限要求：用户")

@PreAuthorize("hasAuthority('user')")
@RestController
@RequestMapping("/user")
public class UserOrderFormController {

    @Autowired
    private JwtAuthService jwtAuthService;

    @Autowired
    private OrderFormService orderFormService;

    /*
    用户下单
     */
    @ApiOperation("用户下单")
    @PostMapping("/orderGoods")
    public RestResult orderGoods(
            @Valid @RequestBody OrderFormVo orderFormVo,
            HttpServletRequest request){
        String username = jwtAuthService.getTokenUsername(request);
        return orderFormService.userOrderGoods(orderFormVo,username);
    }

    /*
    用户获取全部购买订单信息
     */
    @ApiOperation("用户获取全部购买订单信息")
    @GetMapping("/getOrderFormByBuy")
    public RestResult getOrderFormByBuy(HttpServletRequest request){
        String username = jwtAuthService.getTokenUsername(request);
        return orderFormService.findBuyerOrderFormByUsername(username);
    }

    /*
    用户获取全部售卖订单信息
     */
    @ApiOperation("用户获取全部售卖订单信息")
    @GetMapping("/getOrderFormBySeller")
    public RestResult getOrderFormBySeller(HttpServletRequest request){
        String username = jwtAuthService.getTokenUsername(request);
        return orderFormService.findSellerOrderFormByUsername(username);
    }

    /*
    用户获根据id获取订单信息
     */
    @ApiOperation("用户获根据id获取订单信息")
    @GetMapping("/getOrderFormById")
    public RestResult getOrderFormBySeller(
            long id,
            HttpServletRequest request){
        String username = jwtAuthService.getTokenUsername(request);
        return orderFormService.findUserOrderFormById(id,username);
    }

    /*
    用户删除购买订单
     */
    @ApiOperation("用户删除购买订单")
    @DeleteMapping("/deleteOrderFormByBuy")
    public RestResult deleteOrderFormByBuy(@RequestBody List<Long> ids, HttpServletRequest request){
        if (ids.isEmpty()){
            return new RestResult(0,"数据不能为空",null);
        }
        String username = jwtAuthService.getTokenUsername(request);
        return orderFormService.deleteBuyerOrderForm(ids,username);
    }

    /*
    用户删除售卖订单
     */
    @ApiOperation("用户删除售卖订单")
    @DeleteMapping("/deleteOrderFormBySeller")
    public RestResult deleteOrderFormBySeller(@RequestBody List<Long> ids, HttpServletRequest request){
        if (ids.isEmpty()){
            return new RestResult(0,"数据不能为空",null);
        }
        String username = jwtAuthService.getTokenUsername(request);
        return orderFormService.deleteSellerOrderForm(ids,username);
    }

    /*
    用户确认订单
     */
    @ApiOperation("用户确认订单")
    @PutMapping("/confirmUserOrderForm")
    public RestResult confirmUserOrderForm(@RequestBody long id, HttpServletRequest request){
        String username = jwtAuthService.getTokenUsername(request);
        return orderFormService.updateUserOrderFormConfirmed(id,username);
    }

    /*
    买家取消订单
     */
    @ApiOperation("买家取消订单")
    @PutMapping("/cancelOrderFormByBuyer")
    public RestResult cancelOrderFormByBuyer(@RequestBody long id, HttpServletRequest request){
        String username = jwtAuthService.getTokenUsername(request);
        return orderFormService.updateBuyerCancelOrderFormById(id,username);

    }

    /*
    卖家取消订单
     */
    @ApiOperation("卖家取消订单")
    @PutMapping("/cancelOrderFormBySeller")
    public RestResult cancelOrderFormBySeller(@RequestBody long id, HttpServletRequest request){
        String username = jwtAuthService.getTokenUsername(request);
        return orderFormService.updateSellerCancelOrderFormById(id,username);

    }

}
