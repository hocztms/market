package com.hocztms.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.Version;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Goods {


    @TableId(value = "id",type = IdType.AUTO)
    private long id;
    @NotBlank
    private String msg;
    @NotBlank
    private double price;
    @NotBlank
    private String seller;
    @NotBlank
    private double level;
    private Date date;
    private int tag;  //-1审核不通过 0未审核 1审核通过
    private int status;
    @Version//这是一个乐观锁
    private int version;



}
