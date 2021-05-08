package com.hocztms.service;

import com.hocztms.entity.OrderFormCancelRecords;

public interface OrderFormCancelRecordsService {

    OrderFormCancelRecords findRecordsById(Long id);

    OrderFormCancelRecords findRecordsByOrderFormId(Long id);

    Integer insertRecords (OrderFormCancelRecords records);

    Integer updateRecordsConfirmed(OrderFormCancelRecords orderFormCancelRecords);
}
