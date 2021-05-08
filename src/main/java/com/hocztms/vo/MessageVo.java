package com.hocztms.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value = "反馈信息传输类")
public class MessageVo {

    @ApiModelProperty(value = "反馈 id 0代表反馈商品,1代表反馈订单 2代表其他",required = true,example = "2")
    @Pattern(regexp = "^(0|1|2)",message = "")
    private Long objectTag; //判断id类型 方便前端跳转 0商品id 1订单id

    @ApiModelProperty(value = "反馈事务id 如果没有填0",required = true,example = "0")
    @NotNull
    private Long objectId;

    @ApiModelProperty(value = "反馈信息",required = true)
    @NotBlank
    private String msg;
}
