package com.hocztms.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Pattern;

@Data
@AllArgsConstructor
@NoArgsConstructor

@ApiModel
public class PhoneVo {
    /*
    说明 移动：134、135、136、137、138、139、147、150、151、152、157、158、159、172、178、182、183、184、187、188、198
      * 联通：130、131、132、145、155、156、166、171、175、176、185、186、166
      * 电信：133、149、153、173、177、180、181、189、199
      总结 13开头 检查【0-9】 14开头检查 5,7,9 15开头检查 【0-3】,【5-9】 16开头检查 6 17开头检查 【1-3】,【5-8】 18检查【0-9】 19【8,9】
     */
    @ApiModelProperty(value = "电话号码",required = true)
    @Pattern(regexp = "^((13[0-9])|(14[579])|(15([0-3]|[5-9]))|(16[6])|(17([1-3]|[5-8]))|(18[0-8])|(19[89]))\\d{8}$"
            , message = "手机号格式不正确")
    String phone;
}
