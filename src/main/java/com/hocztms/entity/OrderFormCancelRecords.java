package com.hocztms.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "tb_orderFormCancelRecords")
public class OrderFormCancelRecords {
    @TableId(value = "id",type = IdType.AUTO)
    private long id;
    private long formId;
    private int buyerConfirmed;
    private int sellerConfirmed;
}

