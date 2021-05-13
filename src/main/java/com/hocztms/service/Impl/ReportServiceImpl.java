package com.hocztms.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hocztms.common.RestResult;
import com.hocztms.entity.ReportInfo;
import com.hocztms.mapper.ReportInfoMapper;
import com.hocztms.service.*;
import com.hocztms.vo.ReportVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class ReportServiceImpl implements ReportService {
    @Autowired
    private ReportInfoMapper reportInfoMapper;

    @Autowired
    private IllegalUserService illegalUserService;

    @Autowired
    private UserMessageService userMessageService;

    @Autowired
    private UserService userService;

    @Autowired
    private AdminService adminService;

    @Override
    public RestResult userReportIllegalPeople(ReportVo reportVo, String username) {
        try {
            if (userService.findUsersByUsername(reportVo.getIllegalPeople())==null){
                return new RestResult(0,"用户不存在",null);
            }

            if (username.equals(reportVo.getIllegalPeople())){
                adminService.adminFreezeUser(username);
                return new RestResult(1,"你怎么能举报自己呢? 直接帮您冻结了",null);
            }

            ReportInfo reportInfo = new ReportInfo(0,reportVo.getType(),reportVo.getMsg(),reportVo.getIllegalPeople(),username,new Date(),0);
            reportInfoMapper.insert(reportInfo);

            userMessageService.sendAdminReportMessage();
            return new RestResult(1,"操作成功",null);
        }catch (Exception e){
            return new RestResult(0,"操作失败",null);
        }
    }

    @Override
    public RestResult adminGetReportRecords(Long page,Long size) {
        try {
            QueryWrapper<ReportInfo> wrapper = new QueryWrapper<>();
            wrapper.orderByAsc("handled");
            wrapper.orderByDesc("date");
            IPage<ReportInfo> reportInfoIPage = reportInfoMapper.selectPage(new Page<>(page, size), wrapper);
            return new RestResult(1,"操作成功",reportInfoIPage.getRecords());
        }catch (Exception e){
            return new RestResult(0,"操作失败",null);
        }
    }

    @Override
    public RestResult adminPassReport(Long id) {
        try {
            ReportInfo reportInfo = reportInfoMapper.selectById(id);


            if (reportInfo.getHandled()==1){
                return new RestResult(0,"请勿重复操作",null);
            }
            reportInfo.setHandled(1);
            reportInfoMapper.updateById(reportInfo);
            illegalUserService.updateIllegalUserNumByUsername(reportInfo.getIllegalPeople());


            userMessageService.sendUsersMessage(reportInfo.getInformer(),"您举报的用户" + reportInfo.getIllegalPeople() + "已成功" ,0,reportInfo.getId());
            userMessageService.sendUsersMessage(reportInfo.getIllegalPeople(),"您被举报"+reportInfo.getType() + reportInfo.getMsg() + "已经成功受理 违法记录+1",0,0);
            return new RestResult(1,"操作成功",null);
        }catch (Exception e){
            return new RestResult(0,"操作失败",null);
        }
    }

    @Override
    public RestResult adminDeleteReport(List<Long> ids) {
        try {
            for (Long id:ids){
                ReportInfo reportInfo = reportInfoMapper.selectById(id);
                reportInfoMapper.deleteById(id);

                //处理不通过
                if (reportInfo.getHandled()==0){
                    userMessageService.sendUsersMessage(reportInfo.getInformer(),"您举报的用户" + reportInfo.getIllegalPeople() + "失败 证据不充分",0,reportInfo.getId());

                }
            }

            return new RestResult(1,"操作成功",null);
        }catch (Exception e){
            return new RestResult(0,"操作失败",null);
        }
    }
}
