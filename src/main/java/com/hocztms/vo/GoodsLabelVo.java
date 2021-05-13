package com.hocztms.vo;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel
public class GoodsLabelVo {

    @ApiModelProperty(value = "商品id",required = true,example = "0")
    @NotNull
    Long goodsId;

    @ApiModelProperty(value = "标签id 格式如[1,2,...]",required = true,example = "0")
    @NotNull
    List<Long> labelIds;
}
