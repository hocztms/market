package com.hocztms.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Collection {
    @TableId(value = "id",type = IdType.AUTO)
    private long id;

    private String username;

    private long goodsId;

    private String goodsMsg;

    private int status;
}
