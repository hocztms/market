package com.hocztms.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel
public class ReportVo {
    @ApiModelProperty(value = "举报类型",required = true)
    @NotBlank
    String type;

    @ApiModelProperty(value = "举报信息",required = true)
    @NotBlank
    String msg;

    @ApiModelProperty(value = "举报用户名",required = true)
    @NotBlank
    String illegalPeople;
}
