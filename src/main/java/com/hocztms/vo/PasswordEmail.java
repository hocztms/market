package com.hocztms.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
@AllArgsConstructor
@NoArgsConstructor

@ApiModel
public class PasswordEmail {

    @ApiModelProperty(value = "用户名",required = true)
    @NotBlank(message = "用户名不能为空")
    String username;

    @ApiModelProperty(value = "密码",required = true)
    @Pattern(regexp = "(?=.*\\d)(?=.*[a-z])(?=.*[A-Z]).{6,20}",message = "密码长度限制6-20位并且至少包含一个数字或一个大写字母或一个小写字母")
    String password;

    @ApiModelProperty(value = "邮箱密钥",required = true)
    @NotBlank(message = "密钥不能为空")
    String secret;
}
