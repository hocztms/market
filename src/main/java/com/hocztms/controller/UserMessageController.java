package com.hocztms.controller;

import com.hocztms.common.RestResult;
import com.hocztms.service.ReportService;
import com.hocztms.service.UserMessageService;
import com.hocztms.springSecurity.jwt.JwtAuthService;
import com.hocztms.vo.MessageVo;
import com.hocztms.vo.ReportVo;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/user")
@PreAuthorize("hasAnyAuthority('admin','user')")
public class UserMessageController {

    @Autowired
    private JwtAuthService jwtAuthService;

    @Autowired
    private UserMessageService userMessageService;

    @Autowired
    private ReportService reportService;

    /*
    获取用户全部消息
     */
    @ApiOperation("获取用户全部消息")
    @GetMapping("/getMsg")
    public RestResult getUserMsg(HttpServletRequest request){
        String username = jwtAuthService.getTokenUsername(request);
        return userMessageService.findUserMsgByUsername(username);
    }


    /*
    阅读用户消息
     */
    @ApiOperation("阅读用户消息")
    @PutMapping("/readMsg")
    public RestResult readMsg(
            @ApiParam(value = "值传json 格式为\"[id1,id2,id3]\"") @RequestBody List<Long> ids,
            HttpServletRequest request){
        String username = jwtAuthService.getTokenUsername(request);
        return userMessageService.updateUserMessageReadTagByIds(ids,username);
    }


    /*
    删除用户消息
     */
    @ApiOperation("删除用户消息")
    @DeleteMapping("/deleteMsg")
    public RestResult getUserMsg(
            @ApiParam(value = "值传json 格式为\"[id1,id2,id3]\"")@RequestBody List<Long> ids,
            HttpServletRequest request){
        String username = jwtAuthService.getTokenUsername(request);
        return userMessageService.deleteUserMessageByIds(ids,username);
    }

    /*
    用户反馈消息
     */
    @ApiOperation("用户反馈消息")
    @PostMapping("/feedback")
    public RestResult feedback(
            @Valid @RequestBody MessageVo messageVo,
            HttpServletRequest request){
        String username = jwtAuthService.getTokenUsername(request);
        return userMessageService.userFeedback(messageVo,username);
    }

    /*
    用户举报
     */
    @ApiOperation("用户举报")
    @PostMapping("/report")
    public RestResult report(
            @Valid @RequestBody ReportVo reportVo,
            HttpServletRequest request){
        String username = jwtAuthService.getTokenUsername(request);
        return reportService.userReportIllegalPeople(reportVo,username);
    }
}
