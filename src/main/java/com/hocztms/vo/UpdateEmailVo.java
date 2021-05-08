package com.hocztms.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@ApiModel
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateEmailVo {

    @ApiModelProperty(value = "新邮箱",required = true)
    @Email(message = "邮箱格式错误")
    String email;

    @ApiModelProperty(value = "密钥",required = true)
    @NotBlank(message = "密钥不能为空")
    String code;
}
