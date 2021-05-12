package com.hocztms.controller;

import com.hocztms.common.RestResult;
import com.hocztms.service.GoodsService;
import com.hocztms.service.PictureService;
import com.hocztms.service.UserService;
import com.hocztms.springSecurity.jwt.JwtAuthService;
import com.hocztms.vo.GoodsVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

@RestController
@PreAuthorize("hasAuthority('user')")
@RequestMapping("/user")
@Api(tags = "用户商品相关功能方法说明，权限要求：用户")
@Slf4j
public class UserGoodsController {
    @Autowired
    private JwtAuthService jwtAuthService;

    @Autowired
    private PictureService pictureService;

    @Autowired
    private UserService userService;

    @Autowired
    private GoodsService goodsService;


    /*
    用户商品上传图片  tag 为1代表为商品主图片
     */
    @ApiOperation("用户商品上传图片  tag 为1代表为商品主图片")
    @PostMapping("/uploadGoodsPicture")
    public RestResult uploadPicture(
            @ApiParam(value = "商品id") long goodsId,
            @ApiParam(value = "图片类别 1代表主图片 0代表no") int tag,
            @ApiParam(value = "图片") MultipartFile file,
            HttpServletRequest request) {
        if (file == null) {
            return new RestResult(0, "数据不能为空", null);
        } else {
            String username = jwtAuthService.getTokenUsername(request);
            log.info(username + "uploadPicture执行了...");
            return pictureService.insertPictureByUpload(goodsId, username, tag, file);
        }
    }

    /*
    用户创建商品
     */
    @ApiOperation("用户创建商品")
    @PostMapping("/createGoods")
    public RestResult createGoods(
            @Valid @RequestBody GoodsVo goodsVo,
            HttpServletRequest request) {
        log.info("createGoods执行了");
        String username = jwtAuthService.getTokenUsername(request);
        return goodsService.userCreateGoods(goodsVo,username);
    }

    /*
    删除商品标签
     */
    @ApiOperation("删除商品标签")
    @DeleteMapping("/deleteGoodsLabel")
    public RestResult deleteGoodsLabel(
            @ApiParam(value = "商品标签id  传JSON 类似 0")@RequestBody Long id,
            HttpServletRequest request) {
        if(id==null){
            return new RestResult(0,"数据不能为空",null);
        }
        String username = jwtAuthService.getTokenUsername(request);
        return goodsService.deleteGoodsLabelById(id,username);
    }

    /*
    获取商品
     */
    @ApiOperation("获取用户商品")
    @GetMapping("/getUserGoods")
    public RestResult getUserGoods(HttpServletRequest request){
        String username = jwtAuthService.getTokenUsername(request);
        log.info(username + "getUserGoods....");
        return userService.getUserGoods(username);
    }

    /*
    删除商品
     */
    @ApiOperation("删除商品")
    @DeleteMapping("/deleteGoods")
    public RestResult deleteGoods(
            @ApiParam(value = "商品id  传JSON 类似 [0,1,2,3....]") @RequestBody List<Long> ids,
            HttpServletRequest request){
        if (ids.isEmpty()){
            return new RestResult(0,"数据不能为空",null);
        }
        String username = jwtAuthService.getTokenUsername(request);
        return goodsService.deleteUserGoodsByIds(ids,username);
    }

    /*
   添加用户商品标签
    */
    @ApiOperation("添加用户商品标签")
    @DeleteMapping("/addGoodsLabel")
    public RestResult addGoodsLabel(
            @ApiParam(value = "商品id  传JSON 类似 [0,1,2,3....]") @RequestBody List<Long> ids,
            HttpServletRequest request){
        if (ids.isEmpty()){
            return new RestResult(0,"数据不能为空",null);
        }
        String username = jwtAuthService.getTokenUsername(request);
        return goodsService.createGoodsLabel(ids,username);
    }

    /*
    更新商品信息
     */
    @ApiOperation("更新商品信息")
    @PutMapping("/updateGoods")
    public RestResult updateGoods(
            @Valid @RequestBody GoodsVo goodsVo,
            HttpServletRequest request){
        String username = jwtAuthService.getTokenUsername(request);
        return goodsService.updateUserGoods(goodsVo,username);
    }

    /*
    删除商品图片
     */
    @ApiOperation("删除商品图片")
    @DeleteMapping("/deleteGoodsPictures")
    public RestResult updateGoods(
            @ApiParam(value = "商品图片id  传JSON 类似 [0,1,2,3....]") @RequestBody List<Long> ids,
            HttpServletRequest request){
        if (ids.isEmpty()){
            return new RestResult(0,"数据不能为空",null);
        }
        String username = jwtAuthService.getTokenUsername(request);
        return pictureService.deleteGoodsPictureByIds(ids,username);
    }

}
