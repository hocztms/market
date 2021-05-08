package com.hocztms.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hocztms.entity.OrderFormCancelRecords;


import com.hocztms.mapper.OrderFormCancelRecordsMapper;
import com.hocztms.service.OrderFormCancelRecordsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class OrderFormCancelRecordsImpl implements OrderFormCancelRecordsService {

    @Autowired
    private OrderFormCancelRecordsMapper recordsMapper;

    @Override
    public OrderFormCancelRecords findRecordsById(Long id) {
        return recordsMapper.selectById(id);
    }

    @Override
    public OrderFormCancelRecords findRecordsByOrderFormId(Long id) {
        QueryWrapper <OrderFormCancelRecords> wrapper = new QueryWrapper<>();
        wrapper.eq("form_id",id);
        return recordsMapper.selectOne(wrapper);
    }

    @Override
    public Integer insertRecords(OrderFormCancelRecords records) {
        return recordsMapper.insert(records);
    }

    @Override
    public Integer updateRecordsConfirmed(OrderFormCancelRecords orderFormCancelRecords) {
        return recordsMapper.updateById(orderFormCancelRecords);
    }
}
