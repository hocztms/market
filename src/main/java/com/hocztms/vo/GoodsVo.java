package com.hocztms.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel
public class GoodsVo {

    @ApiModelProperty(value = "商品id",required = true,example = "0")
    @NotNull
    Long GoodsId;

    @ApiModelProperty(value = "商品信息",required = true)
    @NotBlank
    String msg;

    @ApiModelProperty(value = "商品价格 不能低于0",required = true,example = "0")
    @NotNull
    @Min(message = "价格最低为0",value = 0)
    Double price;

    @ApiModelProperty(value = "二手成都 区间【0-1】",required = true,example = "0")
    @NotNull
    @Min(message = "二手程度不能低于0",value = 0)
    @Max(message = "二手程度不能高于1",value = 1)
    Double level;

    @ApiModelProperty(value = "标签ids 格式类似与 \"labelIds\":\"[1,2,3...]\"",required = true)
    @NotNull
    List<Long> labelIds;
}
