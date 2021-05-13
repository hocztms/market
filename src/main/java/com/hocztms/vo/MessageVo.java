package com.hocztms.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value = "反馈信息传输类")
public class MessageVo {

    @ApiModelProperty(value = "反馈 id 0代表反馈商品,1代表反馈订单 2代表其他",required = true,example = "2")
    @Max(message = "反馈 id 0代表反馈商品,1代表反馈订单 2代表其他",value = 2)
    @Min(message = "反馈 id 0代表反馈商品,1代表反馈订单 2代表其他",value = 0)
    @NotNull(message = "objectTag不能为空")
    private Long objectTag; //判断id类型 方便前端跳转 0商品id 1订单id

    @ApiModelProperty(value = "反馈事务id 如果没有填0",required = true,example = "0")
    @NotNull(message = "objectId不能为空")
    private Long objectId;

    @ApiModelProperty(value = "反馈信息",required = true)
    @NotBlank(message = "msg不能为空")
    private String msg;
}
