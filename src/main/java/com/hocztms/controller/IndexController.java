package com.hocztms.controller;

import com.hocztms.common.RestResult;
import com.hocztms.service.IndexGoodsService;
import com.hocztms.service.LabelService;
import com.hocztms.service.PictureService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/index")
@PreAuthorize("permitAll()")
@Api(tags = "首页方法说明，权限要求：无")
@Slf4j
public class IndexController {

    @Autowired
    private PictureService pictureService;

    @Autowired
    private LabelService labelService;

    @Autowired
    private IndexGoodsService indexGoodsService;

    /*
    首页获取全部商品 已测试
     */
    @ApiOperation("首页获取全部商品")
    @GetMapping("/getGoods")
    public RestResult getGoods(long page,
                               long size,
                               @ApiParam(value = "0 综合 1价格升序 2价格降序 3二手程度升序 4二手程度降序")@RequestParam(required = false,defaultValue = "0") int mode){
        if (page==0||size==0){
            return new RestResult(0,"数据格式错误",null);
        }
        return indexGoodsService.indexGetGoods(page,size,mode);
    }

    /*
    首页通过标签获取商品 已测试
     */
    @ApiOperation("通过标签获取商品")
    @GetMapping("/getGoodsByLabel")
    public RestResult getGoodsByLabel(
             long page,
             long size,
             long labelId,
             @ApiParam(value = "0 综合 1价格升序 2价格降序 3二手程度升序 4二手程度降序")@RequestParam(required = false,defaultValue = "0") int mode){
        log.info("getGoodsByLabel执行了.....");
        if (page==0||size==0){
            return new RestResult(0,"数据格式错误",null);
        }
        return indexGoodsService.indexGetGoodsByLabel(page,size,labelId,mode);
    }
    /*
    首页通过关键词搜索商品 已测试
     */
    @ApiOperation("首页通过关键词搜索商品")
    @GetMapping("/getGoodsByKeyword")
    public RestResult getGoodsByKeyword(
            long page,
            long size,
            String keyword,
            @ApiParam(value = "0 综合 1价格升序 2价格降序 3二手程度升序 4二手程度降序")@RequestParam(required = false,defaultValue = "0") int mode){
        if (page==0||size==0||keyword==null){
            return new RestResult(0,"数据格式错误",null);
        }
        return indexGoodsService.indexGetGoodsByKeyword(page,size,keyword,mode);
    }

    /*
    首页获取商品主图片 已测试
     */
    @ApiOperation("首页获取商品主图片")
    @GetMapping("/getGoodsMainPicture")
    public RestResult getGoodsMainPicture(long goodsId){
        return pictureService.getGoodsMainPicture(goodsId);
    }

    /*
    根据fid获取标签 已测试
     */
    @ApiOperation("根据fid获取标签")
    @GetMapping("/getLabelByFid")
    public RestResult getLabel(long fid){
        return labelService.getGoodsAllLabelByFid(fid);
    }

}
