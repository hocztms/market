package com.hocztms.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hocztms.common.RestResult;
import com.hocztms.entity.Goods;
import com.hocztms.entity.OrderForm;
import com.hocztms.entity.OrderFormCancelRecords;
import com.hocztms.entity.Users;
import com.hocztms.mapper.OrderFormMapper;
import com.hocztms.service.*;
import com.hocztms.vo.OrderFormVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class OrderFormServiceImpl implements OrderFormService {

    @Autowired
    private GoodsService goodsService;

    @Autowired
    private UserService userService;

    @Autowired
    private OrderFormMapper orderFormMapper;

    @Autowired
    private OrderFormCancelRecordsService recordsService;

    @Autowired
    private UserMessageService userMessageService;


    @Override
    public RestResult userOrderGoods(OrderFormVo orderForm, String username) {
        try {
            if (goodsService.findGoodsByGoodsId(orderForm.getGoodsId()).getSeller().equals(username)){
                return new RestResult(0,"不允许刷单",null);
            }

            if (goodsService.updateOptimisticLockGoods(orderForm.getGoodsId())==0){
                return new RestResult(0,"下单失败",null);
            }

            OrderForm lastForm = initializeGoods(orderForm,username);
            orderFormMapper.insert(lastForm);

            userMessageService.sendUsersMessage(lastForm.getUsername(),"您的商品下单成功 商家联系方式为  " + userService.userContactToStringByUsername(lastForm.getSeller()),1,lastForm.getId());
            userMessageService.sendUsersMessage(lastForm.getSeller(),"您的商品已有新订单 商品id:" + orderForm.getGoodsId() + "买家联系方式为" +userService.userContactToStringByUsername(lastForm.getSeller()),1,lastForm.getId());
            return new RestResult(1,"成功",null);
        }catch (Exception e){
            System.out.println(e.getMessage());
            return new RestResult(0,"下单失败",null);
        }
    }

    @Override
    public RestResult findBuyerOrderFormByUsername(String username) {
        try {
            QueryWrapper<OrderForm> wrapper = new QueryWrapper<>();
            wrapper.eq("username",username);
            wrapper.eq("buyer_deleted",0);
            List<OrderForm> orderForms = orderFormMapper.selectList(wrapper);
            return new RestResult(1,"成功",orderForms);
        }catch (Exception e){
            return new RestResult(0,"失败",null);
        }
    }

    @Override
    public RestResult findSellerOrderFormByUsername(String username) {
        try {
            QueryWrapper<OrderForm> wrapper = new QueryWrapper<>();
            wrapper.eq("seller",username);
            wrapper.eq("seller_deleted",0);
            List<OrderForm> orderForms = orderFormMapper.selectList(wrapper);
            return new RestResult(1,"成功",orderForms);
        }catch (Exception e){
            return new RestResult(0,"失败",null);
        }
    }

    @Override
    public RestResult deleteBuyerOrderForm(List<Long> ids,String username) {
        try {
            for (Long id:ids){
                 OrderForm orderForm = findOrderFormById(id);
                if (orderForm==null){
                    return new RestResult(0,id+"  订单不存在 非法操作",null);
                }
                if (!orderForm.getUsername().equals(username)){
                    return new RestResult(0,id+"  无权限 非法操作",null);
                }
                if (orderForm.getTag()!=1){
                    return new RestResult(0,id + "订单未完成，不能删除",null);
                }
            }

            RestResult result = new RestResult(1,"成功",null);

            for (Long id:ids){
                if (deleteOrderFormById(id,0)==0){
                    result.put("error",id + "删除失败");
                }
            }

            return result;

        }catch (Exception e){
            return new RestResult(0,"失败",null);
        }
    }

    @Override
    public RestResult deleteSellerOrderForm(List<Long> ids, String username) {
        try {
            for (Long id:ids){
                OrderForm orderForm = findOrderFormById(id);
                if (orderForm==null){
                    return new RestResult(0,id+"  订单不存在 非法操作",null);
                }
                if (!orderForm.getSeller().equals(username)){
                    return new RestResult(0,id+"  无权限 非法操作",null);
                }
                if (orderForm.getTag()!=1){
                    return new RestResult(0,id + "订单未完成，不能删除",null);
                }
            }

            RestResult result = new RestResult(1,"成功",null);

            for (Long id:ids){
                if (deleteOrderFormById(id,1)==0){
                    result.put("error",id + "删除失败");
                }
            }

            return result;

        }catch (Exception e){
            return new RestResult(0,"失败",null);
        }
    }

    @Override
    public RestResult updateSellerCancelOrderFormById(Long id, String username) {
        try {
            OrderForm order = findOrderFormById(id);
            if (!order.getSeller().equals(username)){
                return new RestResult(0,"无权限",null);
            }
            if (order.getTag()==1){
                return new RestResult(0,"订单已完成,非法操作",null);
            }

            OrderFormCancelRecords records = recordsService.findRecordsByOrderFormId(order.getId());

            if (records==null){
                recordsService.insertRecords(new OrderFormCancelRecords(0,order.getId(),0,1));
                updateOrderFormTagById(order.getId(),-1);
                userMessageService.sendUsersMessage(order.getUsername(),"您有新的订单通知 订单号为" + order.getId() + "待取消",1,order.getId());
                userMessageService.sendUsersMessage(order.getSeller(),"您的订单号为" + order.getId() + "已经取消等待买家核实",1,order.getId());
                return new RestResult(1,"操作成功,等待买家核实",null);
            }

            if (records.getSellerConfirmed()==1){
                return new RestResult(0,"已确认取消订单,请勿重复操作",null);
            }

            //数据库设置级联 到这层只可能 买家已经取消过 所以可以直接删除
            if (deleteFormById(id)==0||goodsService.updateGoodsStatusById(order.getGoodsId(),1)==0){
                return new RestResult(0,"操作失败",null);
            }

            userMessageService.sendUsersMessage(order.getUsername(),"您的订单号为" + order.getId() + "取消成功",1,order.getId());
            userMessageService.sendUsersMessage(order.getSeller(),"您的订单号为" + order.getId() + "取消成功",1,order.getId());
            return new RestResult(1,"订单已取消",null);
        }catch (Exception e){
            return new RestResult(0,"操作失败",null);
        }
    }

    @Override
    public RestResult updateBuyerCancelOrderFormById(Long id, String username) {
        try {
            OrderForm order = findOrderFormById(id);
            if (!order.getUsername().equals(username)){
                return new RestResult(0,"无权限",null);
            }

            OrderFormCancelRecords records = recordsService.findRecordsByOrderFormId(order.getId());

            if (records==null){
                recordsService.insertRecords(new OrderFormCancelRecords(0,order.getId(),1,0));
                updateOrderFormTagById(order.getId(),-1);
                userMessageService.sendUsersMessage(order.getUsername(),"您的订单号为" + order.getId() + "已经取消等待商家核实",1,order.getId());
                userMessageService.sendUsersMessage(order.getSeller(),"您有新的订单通知 订单号为" + order.getId() + "待取消",1,order.getId());
                return new RestResult(1,"操作成功,等待卖家核实",null);
            }

            if (records.getBuyerConfirmed()==1){
                return new RestResult(0,"已确认取消订单,请勿重复操作",null);
            }

            //数据库设置级联 到这层只可能 卖家已经取消过 所以可以直接删除
            if (deleteFormById(id)==0||goodsService.updateGoodsStatusById(order.getGoodsId(),1)==0){
                return new RestResult(0,"操作失败",null);
            }

            userMessageService.sendUsersMessage(order.getUsername(),"您的订单号为" + order.getId() + "取消成功",1,order.getId());
            userMessageService.sendUsersMessage(order.getSeller(),"您的订单号为" + order.getId() + "取消成功",1,order.getId());
            return new RestResult(1,"订单已取消",null);
        }catch (Exception e){
            return new RestResult(0,"操作失败",null);
        }
    }

    @Override
    public RestResult updateUserOrderFormConfirmed(Long id, String username) {
        try {
            OrderForm orderForm = findOrderFormById(id);

            if (!orderForm.getUsername().equals(username)){
                return new RestResult(0,"无权限",null);
            }

            orderForm.setTag(1);
            if (orderFormMapper.updateById(orderForm)==0){
                return new RestResult(0,"确认失败",null);
            }
            userMessageService.sendUsersMessage(orderForm.getUsername(),"您的订单号为" + orderForm.getId() + "已完成",1,orderForm.getId());
            userMessageService.sendUsersMessage(orderForm.getSeller(),"您的订单号为" + orderForm.getId() + "已完成",1,orderForm.getId());
            return new RestResult(1,"确认成功",null);
        }catch (Exception e){
            return new RestResult(0,"失败",null);
        }
    }

    @Override
    public RestResult findUserOrderFormById(Long id, String username) {
        try {
            OrderForm orderFormById = findOrderFormById(id);

            if (!orderFormById.getSeller().equals(username)&&!orderFormById.getUsername().equals(username)){
                return new RestResult(0,"无权限",null);
            }

            return new RestResult(1,"操作成功",null);
        }catch (Exception e){
            return new RestResult(0,"操作失败",null);
        }

    }

    @Override
    public OrderForm initializeGoods(OrderFormVo orderFormVo,String username) {
        Goods good = goodsService.findGoodsByGoodsId(orderFormVo.getGoodsId());
        Users seller = userService.findUsersByUsername(good.getSeller());
        if (seller == null){
            throw new RuntimeException("出现了一点错误");
        }
        return new OrderForm(0,username,orderFormVo.getGoodsId(),orderFormVo.getBuyer(),orderFormVo.getBuyerAddress(),orderFormVo.getBuyerAddress(),seller.getUsername(),seller.getPhone(),new Date(),0,orderFormVo.getWay(),0,0);
    }

    @Override
    public OrderForm findOrderFormById(Long id) {
        return orderFormMapper.selectById(id);
    }

    @Override//model 0 为用户删除 1 为商家删除
    public Integer deleteOrderFormById(Long id,int model) {
        OrderForm orderForm = orderFormMapper.selectById(id);
        if (model==0){
            orderForm.setBuyerDeleted(1);
        }
        else {
            orderForm.setSellerDeleted(1);
        }
        return orderFormMapper.updateById(orderForm);
    }

    @Override
    public Integer updateOrderFormTagById(Long id, int tag) {
        OrderForm orderFormById = findOrderFormById(id);
        orderFormById.setTag(tag);
        return orderFormMapper.updateById(orderFormById);
    }

    @Override
    public Integer deleteFormById(Long id) {
        return orderFormMapper.deleteById(id);
    }
}
