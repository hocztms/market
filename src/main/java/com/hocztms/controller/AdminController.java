package com.hocztms.controller;


import com.alibaba.fastjson.JSONObject;
import com.hocztms.common.RestResult;
import com.hocztms.entity.Label;
import com.hocztms.service.AdminService;
import com.hocztms.service.GoodsService;
import com.hocztms.service.ReportService;
import com.hocztms.springSecurity.jwt.JwtAuthService;
import com.hocztms.utils.ResultUtils;
import com.hocztms.vo.IllegalGoodsVo;
import com.hocztms.vo.IllegalUserVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

@RestController
@PreAuthorize("hasAuthority('admin')")
@RequestMapping("/admin")
@Slf4j
@Api(tags = "管理员功能方法说明，权限要求：管理员")
public class AdminController {

    @Autowired
    private JwtAuthService jwtAuthService;

    @Autowired
    private GoodsService goodsService;

    @Autowired
    private AdminService adminService;

    @Autowired
    private ReportService reportService;

    /*
    管理员权限测试接口
     */
    @ApiOperation("管理员权限测试接口")
    @GetMapping("/hello")
    public String admin(HttpServletRequest request){
        return "hello "+jwtAuthService.getTokenUsername(request);
    }

    /*
    管理员获取未审核商品 已测试
     */
    @ApiOperation("管理员获取未审核商品")
    @GetMapping("/getGoods")
    public RestResult getGoods(
            @ApiParam(value = "页数") long page,
            @ApiParam(value = "每页大小") long size
    ){
        if (page==0||size==0){
            return ResultUtils.error(0,"数据非法");
        }
        return adminService.adminGetGoods(page,size);
    }

    /*
    假删除审核不通过商品 已测试
     */
    @ApiOperation("假删除审核不通过商品")
    @DeleteMapping("/deleteGoods")
    public RestResult deleteGoods(
            @Valid @RequestBody IllegalGoodsVo goodsVo){
        if (goodsVo==null){
            return ResultUtils.error(0,"数据不能为空");
        }
        else {
            log.info(goodsVo.toString());
            return adminService.adminDeleteGoods(goodsVo);
        }
    }

    /*
    审核通过商品  已测试
     */
    @ApiOperation("审核通过商品")
    @PutMapping("/passGoods")
    public RestResult passGoods(
            @ApiParam(value = "JSON数据 格式 例如 1")@RequestBody Long goodsId){
        if (goodsId==null){
            return ResultUtils.error(0,"数据不能为空");
        }
        else {
            return adminService.adminPassGoods(goodsId);
        }
    }

    /*
    获取用户违法记录
     */
    @ApiOperation("获取用户违法记录")
    @GetMapping("/getUsers")
    public RestResult adminGetIllegalUser(
            @ApiParam(value = "页数") long page,
            @ApiParam(value = "每页大小") long size){
        if (page==0||size==0){
            return ResultUtils.error(0,"数据非法");
        }
        return adminService.adminGetIllegalUser(page,size);
    }

    /*
    通过用户名获取用户非法记录
     */
    @ApiOperation("通过用户名获取用户非法记录")
    @GetMapping("/findUserByUsername")
    public RestResult adminFindIllegalUserByUsername(String username){
        return adminService.adminGetIllegalUserByUsername(username);
    }

    /*
    冻结账户 已经测试
     */
    @ApiOperation("冻结账户")
    @PutMapping("/freezeUser")
    public RestResult adminFreezeUser(
            @ApiParam(value = "格式 {\"username\":\"username\"}") @RequestBody String JsonUsername){
        String username = JSONObject.parseObject(JsonUsername).getObject("username",String.class);
        if (username==null){
            return ResultUtils.error(0,"数据非法");
        }
        return adminService.adminFreezeUser(username);
    }

    /*
    解冻账户 已经测试
     */
    @ApiOperation("解冻账户")
    @PutMapping("/unFreezeUser")
    public RestResult adminUnFreezeUser(
            @ApiParam(value = "格式 {\"username\":\"username\"}") @RequestBody String JsonUsername
    ){
        String username = JSONObject.parseObject(JsonUsername).getObject("username",String.class);
        if (username==null){
            return ResultUtils.error(0,"数据非法");
        }
        return adminService.adminUnFreezeUser(username);
    }


    /*
    增加用户 违法记录  应用场景：用户恶意举报反馈
     */
    @ApiOperation("增加用户 违法记录  应用场景：用户恶意举报反馈")
    @PutMapping("/upUserIllegalNum")
    public RestResult upUserIllegalNum(
            @Valid @RequestBody IllegalUserVo illegalUserVo
            ){
        return adminService.adminUpUserIllegalNum(illegalUserVo);
    }

    /*
    管理员删除标签  已测试
     */
    @ApiOperation("管理员删除标签")
    @DeleteMapping("/deleteLabel")
    public RestResult deleteLabel(
            @ApiParam(value = "JSON数据 格式 例如 1") @RequestBody Long id){

        return adminService.adminDeleteLabelById(id);
    }

    /*
    管理员更新标签  已测试
     */
    @ApiOperation("管理员更新标签")
    @PutMapping("/updateLabel")
    public RestResult updateLabel(@Valid @RequestBody Label label){

        return adminService.adminUpdateLabel(label);
    }

    /*
    管理员创建标签  已测试
     */
    @ApiOperation("管理员创建标签")
    @PostMapping("/createLabel")
    public RestResult createLabel(@Valid @RequestBody Label label){
        return adminService.adminInsertLabel(label);
    }

    /*
    管理员获取举报信息  已测试
     */
    @ApiOperation("管理员获取举报信息")
    @GetMapping("/getReport")
    public RestResult getReport(
            @ApiParam(value = "页数") long page,
            @ApiParam(value = "每页大小") long size
    ){

        return reportService.adminGetReportRecords(page,size);
    }

    /*
    管理员通过举报  已测试
     */
    @ApiOperation("管理员通过举报")
    @PutMapping("/passReport")
    public RestResult passReport(
            @ApiParam(value = "JSON数据 格式 例如 1") @RequestBody Long id
    ){
        return reportService.adminPassReport(id);
    }


    /*
    管理员删除举报信息  已测试
     */
    @ApiOperation("管理员删除举报信息")
    @DeleteMapping("/deleteReport")
    public RestResult deleteReport(
            @ApiParam(value = "商品id  传JSON 类似 [0,1,2,3....]") @RequestBody List<Long> ids
    ){
        if (ids.isEmpty()){
            return ResultUtils.error(0,"数据非法");
        }
        return reportService.adminDeleteReport(ids);
    }

}
