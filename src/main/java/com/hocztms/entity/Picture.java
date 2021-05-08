package com.hocztms.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Picture {

    @TableId(value = "id",type = IdType.AUTO)
    private long id;
    private long goodsId;  //商品id
    private String username;
    private Date date;
    private String picturename;
    private int tag;
}
