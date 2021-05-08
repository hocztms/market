package com.hocztms.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "tb_message")
public class Message {

    @TableId(value = "id",type = IdType.AUTO)
    private long id;
    private long objectTag; //判断id类型 方便前端跳转 0商品id 1订单id
    private long objectId;
    private String username;
    private String msg;
    private String sender;
    private Date date;
    private int readTag;

}
