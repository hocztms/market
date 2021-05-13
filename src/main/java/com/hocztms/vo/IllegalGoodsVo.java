package com.hocztms.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel
public class IllegalGoodsVo {

    @ApiModelProperty(value = "商品id",required = true,example = "0")
    @NotNull(message = "不允许数据为空")
    @Min(value = 1,message = "不允许输入非法数据")
    Long goodsId;

    @ApiModelProperty(value = "商品信息",required = true,example = "this is haha")
    @NotBlank(message = "不允许数据为空")
    String msg;
}
