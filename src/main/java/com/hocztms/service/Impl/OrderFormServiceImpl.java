package com.hocztms.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hocztms.common.RestResult;
import com.hocztms.entity.Goods;
import com.hocztms.entity.OrderForm;
import com.hocztms.entity.OrderFormCancelRecords;
import com.hocztms.entity.Users;
import com.hocztms.mapper.OrderFormMapper;
import com.hocztms.service.*;
import com.hocztms.utils.ResultUtils;
import com.hocztms.vo.OrderFormVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
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
            Goods goods = goodsService.findGoodsByGoodsId(orderForm.getGoodsId());
            if (goods==null||goods.getStatus()!=1||goods.getTag()!=1){
                return ResultUtils.error(0,"商品当前状态不可购买!");
            }

            if (goods.getSeller().equals(username)){
                return ResultUtils.error(0,"不允许刷单");
            }

            if (goodsService.updateOptimisticLockGoods(orderForm.getGoodsId())==0){
                return ResultUtils.error(0,"下单失败,商品已售出");
            }

            OrderForm lastForm = this.initializeOrderForm(orderForm,username);
            orderFormMapper.insert(lastForm);

            userMessageService.sendUsersMessage(lastForm.getUsername(),"您的商品下单成功 商家联系方式为  " + userService.userContactToStringByUsername(lastForm.getSeller()),1,lastForm.getId());
            userMessageService.sendUsersMessage(lastForm.getSeller(),"您的商品已有新订单 商品id:" + orderForm.getGoodsId() + "买家联系方式为" +userService.userContactToStringByUsername(lastForm.getSeller()),1,lastForm.getId());
            return ResultUtils.success();
        }catch (Exception e){
            log.warn(e.getMessage());
            return ResultUtils.error(-1,"error");
        }
    }

    @Override
    public RestResult findBuyerOrderFormByUsername(String username) {
        try {
            QueryWrapper<OrderForm> wrapper = new QueryWrapper<>();
            wrapper.eq("username",username);
            wrapper.eq("buyer_deleted",0);
            List<OrderForm> orderForms = orderFormMapper.selectList(wrapper);
            return ResultUtils.success(orderForms);
        }catch (Exception e){
            log.warn(e.getMessage());
            return ResultUtils.error(-1,"error");
        }
    }

    @Override
    public RestResult findSellerOrderFormByUsername(String username) {
        try {
            QueryWrapper<OrderForm> wrapper = new QueryWrapper<>();
            wrapper.eq("seller",username);
            wrapper.eq("seller_deleted",0);
            List<OrderForm> orderForms = orderFormMapper.selectList(wrapper);
            return ResultUtils.success(orderForms);
        }catch (Exception e){
            log.warn(e.getMessage());
            return ResultUtils.error(-1,"error");
        }
    }

    @Override
    public RestResult deleteBuyerOrderForm(List<Long> ids,String username) {
        try {

            Map<Integer,String> errors = new HashMap<>();
            int i=1;

            for (Long id:ids){
                 OrderForm orderForm = findOrderFormById(id);
                if (orderForm==null){
                    errors.put(i++," id:"+id+" 订单不存在 非法操作");
                }
                else if (!orderForm.getUsername().equals(username)){
                    errors.put(i++," id:"+id+" 无权限");
                }
                else if (orderForm.getTag()!=1){
                    errors.put(i++," id:"+id+" 当前状态不可取消");
                }
                else {
                    //model 来方便判别 username 还是 seller
                    if (deleteOrderFormById(id,0)==0){
                        errors.put(i++," id:"+id+" 删除失败");
                    }
                    }
                }

            if(!errors.isEmpty()){
                RestResult result = new RestResult(1,"部分失败",null);
                result.put("errors",errors);
                return result;
            }
            return ResultUtils.success();
        }catch (Exception e){
            log.warn(e.getMessage());
            return ResultUtils.error(-1,"error");
        }
    }

    @Override
    public RestResult deleteSellerOrderForm(List<Long> ids, String username) {
        try {
            Map<Integer,String> errors = new HashMap<>();
            int i=1;

            for (Long id:ids){
                OrderForm orderForm = findOrderFormById(id);
                if (orderForm==null){
                    errors.put(i++," id:"+id+" 订单不存在 非法操作");
                }
                else if (!orderForm.getSeller().equals(username)){
                    errors.put(i++," id:"+id+" 无权限");
                }
                else if (orderForm.getTag()!=1){
                    errors.put(i++," id:"+id+" 当前状态不可取消");
                }
                else {
                    //model 来方便判别 username 还是 seller
                    if (deleteOrderFormById(id,1)==0){
                        errors.put(i++," id:"+id+" 删除失败");
                    }
                    }
                }

            if(!errors.isEmpty()){
                RestResult result = new RestResult(1,"部分失败",null);
                result.put("errors",errors);
                return result;
            }
            return ResultUtils.success();
        }catch (Exception e){
            log.warn(e.getMessage());
            return ResultUtils.error(-1,"error");
        }
    }

    @Override
    public RestResult updateSellerCancelOrderFormById(Long id, String username) {
        try {
            OrderForm order = findOrderFormById(id);
            if (!order.getSeller().equals(username)){
                return ResultUtils.error(0,"无权限");
            }

            if (order.getTag()==1){
                return ResultUtils.error(0,"订单已完成,非法操作");
            }

            if (order.getTag()==-1){
                return ResultUtils.error(0,"请勿重复提交");
            }

            OrderFormCancelRecords records = recordsService.findRecordsByOrderFormId(order.getId());

            if (records==null){
                recordsService.insertRecords(new OrderFormCancelRecords(0,order.getId(),0,1));
                updateOrderFormTagById(order.getId(),-1);

                userMessageService.sendUsersMessage(order.getUsername(),"您有新的订单通知 订单号为" + order.getId() + "待取消",1,order.getId());
                userMessageService.sendUsersMessage(order.getSeller(),"您的订单号为" + order.getId() + "已经取消等待买家核实",1,order.getId());
                return ResultUtils.success("操作成功,等待买家核实",null);
            }

            if (records.getSellerConfirmed()==1){
                return ResultUtils.error(0,"已确认取消订单,请勿重复操作");
            }

            //数据库设置级联 到这层只可能 买家已经取消过 所以可以直接删除
            if (deleteFormById(id)==0||goodsService.updateGoodsStatusById(order.getGoodsId(),1)==0){
                return ResultUtils.error(0,"操作失败");
            }

            userMessageService.sendUsersMessage(order.getUsername(),"您的订单号为" + order.getId() + "取消成功",1,order.getId());
            userMessageService.sendUsersMessage(order.getSeller(),"您的订单号为" + order.getId() + "取消成功",1,order.getId());
            return ResultUtils.success();
        }catch (Exception e){
            log.warn(e.getMessage());
            return ResultUtils.error(-1,"error");
        }
    }

    @Override
    public RestResult updateBuyerCancelOrderFormById(Long id, String username) {
        try {
            OrderForm order = findOrderFormById(id);
            if (!order.getUsername().equals(username)){
                return ResultUtils.error(0,"无权限");
            }

            if (order.getTag()==1){
                return ResultUtils.error(0,"订单已完成,非法操作");
            }

            if (order.getTag()==-1){
                return ResultUtils.error(0,"请勿重复提交");
            }

            OrderFormCancelRecords records = recordsService.findRecordsByOrderFormId(order.getId());

            if (records==null){
                recordsService.insertRecords(new OrderFormCancelRecords(0,order.getId(),1,0));
                updateOrderFormTagById(order.getId(),-1);
                userMessageService.sendUsersMessage(order.getUsername(),"您的订单号为" + order.getId() + "已经取消等待商家核实",1,order.getId());
                userMessageService.sendUsersMessage(order.getSeller(),"您有新的订单通知 订单号为" + order.getId() + "待取消",1,order.getId());
                return ResultUtils.success("操作成功,等待卖家核实",null);
            }

            if (records.getBuyerConfirmed()==1){
                return ResultUtils.error(0,"已确认取消订单,请勿重复操作");
            }

            //数据库设置级联 到这层只可能 卖家已经取消过 所以可以直接删除
            if (deleteFormById(id)==0||goodsService.updateGoodsStatusById(order.getGoodsId(),1)==0){
                return ResultUtils.error(0,"操作失败");
            }

            userMessageService.sendUsersMessage(order.getUsername(),"您的订单号为" + order.getId() + "取消成功",1,order.getId());
            userMessageService.sendUsersMessage(order.getSeller(),"您的订单号为" + order.getId() + "取消成功",1,order.getId());
            return ResultUtils.success();
        }catch (Exception e){
            log.warn(e.getMessage());
            return ResultUtils.error(-1,"error");
        }
    }

    @Override
    public RestResult updateUserOrderFormConfirmed(Long id, String username) {
        try {
            OrderForm orderForm = findOrderFormById(id);

            if (!orderForm.getUsername().equals(username)){
                return ResultUtils.error(0,"无权限");
            }
            if (orderForm.getTag()==1){
                return ResultUtils.error(0,"请勿重新操作");
            }

            if (orderForm.getTag()==-1){
                return ResultUtils.error(0,"当前状态不能取消");
            }

            orderForm.setTag(1);
            if (orderFormMapper.updateById(orderForm)==0){
                return ResultUtils.error(0,"确认失败");
            }
            userMessageService.sendUsersMessage(orderForm.getUsername(),"您的订单号为" + orderForm.getId() + "已完成",1,orderForm.getId());
            userMessageService.sendUsersMessage(orderForm.getSeller(),"您的订单号为" + orderForm.getId() + "已完成",1,orderForm.getId());
            return ResultUtils.success();
        }catch (Exception e){
            log.warn(e.getMessage());
            return ResultUtils.error(-1,"error");
        }
    }

    @Override
    public RestResult findUserOrderFormById(Long id, String username) {
        try {
            OrderForm orderFormById = findOrderFormById(id);

            if (!orderFormById.getSeller().equals(username)&&!orderFormById.getUsername().equals(username)){
                return ResultUtils.error(0,"无权限");
            }

            return ResultUtils.success(orderFormById);
        }catch (Exception e){
            log.warn(e.getMessage());
            return ResultUtils.error(-1,"error");
        }

    }

    @Override
    public OrderForm initializeOrderForm(OrderFormVo orderFormVo,String username) {
        Goods good = goodsService.findGoodsByGoodsId(orderFormVo.getGoodsId());
        Users seller = userService.findUsersByUsername(good.getSeller());
        if (seller == null){
            throw new RuntimeException("出现了一点错误");
        }
        return new OrderForm(0,username,orderFormVo.getGoodsId(),orderFormVo.getBuyer(),orderFormVo.getBuyerAddress(),orderFormVo.getBuyerAddress(),seller.getUsername(),seller.getPhone(),new Date(),orderFormVo.getWay(),0,0,0);
    }

    @Override
    public OrderForm findOrderFormById(Long id) {
        return orderFormMapper.selectById(id);
    }

    @Override
    public OrderForm findOrderFormByGoodsId(Long id) {
        QueryWrapper <OrderForm> wrapper = new QueryWrapper<>();
        wrapper.eq("goods_id",id);
        return orderFormMapper.selectOne(wrapper);
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
