package com.hocztms.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "tb_goodsLabel")
public class GoodsLabel {
    @TableId(value = "id",type = IdType.AUTO)
    private long id;
    @NotNull
    private long labelId;
    private String label;
    @NotNull
    private long goodsId;
}
