package com.hocztms.controller;

import com.hocztms.common.RestResult;
import io.swagger.annotations.Api;
import com.hocztms.service.GoodsService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "商品方法，权限要求 管理员 或者 用户")
@PreAuthorize("hasAnyAuthority('admin','user')")
@RestController
@RequestMapping("/goods")
public class GoodsController {

    @Autowired
    private GoodsService goodsService;

    /*
    获取商品详细信息 已测试
     */
    @ApiOperation("获取商品详细信息")
    @GetMapping("/getDetails")
    public RestResult getGoodsDetails(long goodsId){
        return goodsService.getGoodsDetails(goodsId);
    }

    /*
    获取商品标签 已测试
     */
    @ApiOperation("获取商品标签")
    @GetMapping("/getGoodsLabel")
    public RestResult getGoodsLabel(long goodsId){
        return goodsService.findGoodsLabelById(goodsId);
    }

}
