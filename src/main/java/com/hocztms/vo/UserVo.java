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
//注册传输类
@ApiModel
public class UserVo {

    @ApiModelProperty(value = "用户名",required = true)
    @Pattern(regexp="^[\u4e00-\u9fa5_a-zA-Z0-9]+$",message="用户名只能有中文,数字,字母")
    @Size(min = 1,max = 20,message = "用户名不能超过20位")
    private String username;

    @ApiModelProperty(value = "密码",required = true)
    @Pattern(regexp = "(?=.*\\d)(?=.*[a-z])(?=.*[A-Z]).{6,20}",message = "密码长度限制6-20位并且至少包含一个数字或一个大写字母或一个小写字母")
    private String password;

    @ApiModelProperty(value = "邮箱",required = true)
    @Email(message = "邮箱格式不正确")
    private String email;

    /*
    说明 移动：134、135、136、137、138、139、147、150、151、152、157、158、159、172、178、182、183、184、187、188、198
      * 联通：130、131、132、145、155、156、166、171、175、176、185、186、166
      * 电信：133、149、153、173、177、180、181、189、199
      总结 13开头 检查【0-9】 14开头检查 5,7,9 15开头检查 【0-3】,【5-9】 16开头检查 6 17开头检查 【1-3】,【5-8】 18检查【0-9】 19【8,9】
     */
    @ApiModelProperty(value = "电话号码",required = true)
    @Pattern(regexp = "^((13[0-9])|(14[579])|(15([0-3]|[5-9]))|(16[6])|(17([1-3]|[5-8]))|(18[0-8])|(19[89]))\\d{8}$"
            , message = "手机号格式不正确")
    private String phone;

    @ApiModelProperty(value = "验证码",required = true)
    @NotBlank(message = "验证码不能为空")
    private String code;
}
