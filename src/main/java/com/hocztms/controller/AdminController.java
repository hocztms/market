package com.hocztms.controller;


import com.hocztms.common.RestResult;
import com.hocztms.entity.Label;
import com.hocztms.service.AdminService;
import com.hocztms.service.GoodsService;
import com.hocztms.service.ReportService;
import com.hocztms.springSecurity.jwt.JwtAuthService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

@RestController
@PreAuthorize("hasAuthority('admin')")
@RequestMapping("/admin")
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
    管理员获取未审核商品
     */
    @ApiOperation("管理员获取未审核商品")
    @GetMapping("/getGoods")
    public RestResult getGoods(long page,long size,String orderBy,int model){
        if (page==0||size==0||!goodsService.checkOrderBy(orderBy)){
            return new RestResult(0,"数据传输错误",null);
        }
        return adminService.adminGetGoods(page,size,orderBy,model);
    }

    /*
    假删除审核不通过商品
     */
    @ApiOperation("假删除审核不通过商品")
    @PostMapping("/deleteGoods")
    public RestResult deleteGoods(@RequestBody Long goodsId){
        if (goodsId==null){
            return new RestResult   (0,"数据不能为空",null);
        }
        else {
            return adminService.adminDeleteGoods(goodsId);
        }
    }

    /*
    审核通过商品
     */
    @ApiOperation("审核通过商品")
    @PostMapping("/passGoods")
    public RestResult passGoods(@RequestBody Long goodsId){
        if (goodsId==null){
            return new RestResult(0,"数据不能为空",null);
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
    public RestResult adminGetIllegalUser(long page,long size){
        if (page==0||size==0){
            return new RestResult(0,"数据格式错误",null);
        }
        return adminService.adminGetIllegalUser(page,size);
    }

    /*
    冻结账户
     */
    @ApiOperation("冻结账户")
    @PostMapping("/freezeUser")
    public RestResult adminFreezeUser(@RequestBody String username){
        if (username==null){
            return new RestResult(0,"数据格式错误",null);
        }
        return adminService.adminFreezeUser(username);
    }

    /*
    解冻账户
     */
    @ApiOperation("解冻账户")
    @PostMapping("/unFreezeUser")
    public RestResult adminUnFreezeUser(@RequestBody String username){
        if (username==null){
            return new RestResult(0,"数据格式错误",null);
        }
        return adminService.adminUnFreezeUser(username);
    }

    /*
    管理员删除标签
     */
    @ApiOperation("管理员删除标签")
    @PostMapping("/deleteLabel")
    public RestResult deleteLabel(@RequestBody long id){

        return adminService.adminDeleteLabelById(id);
    }

    /*
    管理员更新标签
     */
    @ApiOperation("管理员更新标签")
    @PutMapping("/updateLabel")
    public RestResult updateLabel(@Valid @RequestBody Label label){

        return adminService.adminUpdateLabel(label);
    }

    /*
    管理员创建标签
     */
    @ApiOperation("管理员创建标签")
    @PostMapping("/creatLabel")
    public RestResult creatLabel(@Valid @RequestBody Label label){
        return adminService.adminInsertLabel(label);
    }

    /*
    管理员获取举报信息
     */
    @ApiOperation("管理员获取举报信息")
    @GetMapping("/getReport")
    public RestResult getReport(
            long page,
            long size
    ){

        return reportService.adminGetReportRecords(page,size);
    }

    /*
    管理员通过举报
     */
    @ApiOperation("管理员通过举报")
    @PostMapping("/passReport")
    public RestResult passReport(
            Long id
    ){

        return reportService.adminPassReport(id);
    }


    /*
    管理员删除举报信息
     */
    @ApiOperation("管理员删除举报信息")
    @PostMapping("/deleteReport")
    public RestResult deleteReport(
            List<Long> ids
    ){
        if (ids.isEmpty()){
            return new RestResult(0,"数据不能为空",null);
        }
        return reportService.adminDeleteReport(ids);
    }

}
