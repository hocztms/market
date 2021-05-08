package com.hocztms.controller;

import com.hocztms.common.RestResult;
import com.hocztms.service.UserCollectionService;
import com.hocztms.springSecurity.jwt.JwtAuthService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@PreAuthorize("hasAuthority('user')")
@RequestMapping("/user")
@Api(tags = "收藏品相关功能方法说明，权限要求：用户")
public class UserCollectionController {

    @Autowired
    private JwtAuthService jwtAuthService;

    @Autowired
    private UserCollectionService collectionService;


    /*
    用户收藏商品
     */
    @ApiOperation("用户收藏商品")
    @PostMapping("/collectGoods")
    public RestResult collectGoods(
            @ApiParam(value = "直接传goodsId 例如 直接 传个1就行") @RequestBody Long goodsId, HttpServletRequest request){
        String username = jwtAuthService.getTokenUsername(request);
        return collectionService.userCollectGoods(goodsId,username);
    }

    /*
    用户获取收藏列表
     */
    @ApiOperation("用户收藏商品")
    @GetMapping("/getCollection")
    public RestResult getCollection(
            long page,
            long size,
            HttpServletRequest request){
        String username = jwtAuthService.getTokenUsername(request);
        return collectionService.findUserCollectionByUsername(page,size,username);


    }

    /*
    用户删除商品收藏
     */
    @ApiOperation("用户删除商品收藏")
    @GetMapping("/deleteCollection")
    public RestResult deleteCollection(
            @ApiParam(value = "收藏品ids  传格式类似[1,2,3,....]")@RequestBody List<Long> ids,
            HttpServletRequest request){
        String username = jwtAuthService.getTokenUsername(request);
        return collectionService.deleteUserCollectionByIds(ids,username);
    }

}
