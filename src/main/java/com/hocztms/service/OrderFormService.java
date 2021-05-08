package com.hocztms.service;

import com.hocztms.common.RestResult;
import com.hocztms.entity.OrderForm;
import com.hocztms.vo.OrderFormVo;

import java.util.List;

public interface OrderFormService {

    RestResult userOrderGoods(OrderFormVo orderForm, String username);

    RestResult findBuyerOrderFormByUsername(String username);

    RestResult findSellerOrderFormByUsername(String username);

    RestResult deleteBuyerOrderForm(List<Long> ids, String username);

    RestResult deleteSellerOrderForm(List<Long> ids, String username);

    RestResult updateSellerCancelOrderFormById(Long id, String username);

    RestResult updateBuyerCancelOrderFormById(Long id, String username);

    RestResult updateUserOrderFormConfirmed(Long id, String username);

    RestResult findUserOrderFormById(Long id, String username);

    OrderForm initializeGoods(OrderFormVo orderFormVo, String username);

    OrderForm findOrderFormById(Long id);

    Integer deleteOrderFormById(Long id, int model);

    Integer updateOrderFormTagById(Long id,int tag);

    Integer deleteFormById(Long id);

}
