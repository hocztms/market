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
@ApiModel
public class OrderFormVo {

    @ApiModelProperty(value = "商品id",required = true,example = "0")
    @NotNull(message = "商品id不能为空")
    private Long goodsId;

    @ApiModelProperty(value = "买家地址",required = true)
    @NotBlank(message = "买家不允许为空")
    private String buyer;

    @ApiModelProperty(value = "买家电话",required = true)
    /*
    说明 移动：134、135、136、137、138、139、147、150、151、152、157、158、159、172、178、182、183、184、187、188、198
      * 联通：130、131、132、145、155、156、166、171、175、176、185、186、166
      * 电信：133、149、153、173、177、180、181、189、199
      总结 13开头 检查【0-9】 14开头检查 5,7,9 15开头检查 【0-3】,【5-9】 16开头检查 6 17开头检查 【1-3】,【5-8】 18检查【0-9】 19【8,9】
     */
    @Pattern(regexp = "^((13[0-9])|(14[579])|(15([0-3]|[5-9]))|(16[6])|(17([1-3]|[5-8]))|(18[0-8])|(19[89]))\\d{8}$"
            , message = "手机号格式不正确")
    private String buyerPhone;

    @ApiModelProperty(value = "买家地址",required = true)
    @NotBlank(message = "买家地址不允许为空")
    private String buyerAddress;

    @ApiModelProperty(value = "送货方式 0代表 自提 1代表送货上门",example = "0")
    @NotNull(message = "送货方式不允许为空")
    @Max(message = "0代表 自提 1代表送货上门",value = 1)
    @Min(message = "0代表 自提 1代表送货上门",value = 0)
    private Integer way;
}
