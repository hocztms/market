package com.hocztms.entity;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "tb_orderForm")
public class OrderForm {

    @TableId(value = "id",type = IdType.AUTO)
    private long id;
    private String username;
    private long goodsId;
    private String buyer;
    private String address;
    private String buyerPhone;
    private String seller;
    private String sellerPhone;
    private Date date;
    private int way;
    private int tag;
    private int buyerDeleted; //假删除买家
    private int sellerDeleted; //假删除卖家
}
