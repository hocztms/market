package com.hocztms.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hocztms.common.RestResult;
import com.hocztms.entity.ReportInfo;
import com.hocztms.mapper.ReportInfoMapper;
import com.hocztms.service.*;
import com.hocztms.utils.ResultUtils;
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
            if (getUserReportInfoNum(username)>=5){
                userMessageService.sendUsersMessage(username,"您举报过多 消停消停",2,0);
                return ResultUtils.error(0,"当前举报次数过多不允许举报");
            }
            if (userService.findUsersByUsername(reportVo.getIllegalPeople())==null){
                return ResultUtils.error(0,"用户不存在");
            }

            if (username.equals(reportVo.getIllegalPeople())){
                return ResultUtils.error(0,"哥们想开点 别举报自己");
            }

            ReportInfo reportInfo = new ReportInfo(0,reportVo.getType(),reportVo.getMsg(),reportVo.getIllegalPeople(),username,new Date(),0);
            reportInfoMapper.insert(reportInfo);

            userMessageService.sendAdminReportMessage();
            return ResultUtils.success();
        }catch (Exception e){
            return ResultUtils.error(-1,"error");
        }
    }

    @Override
    public RestResult adminGetReportRecords(Long page,Long size) {
        try {
            QueryWrapper<ReportInfo> wrapper = new QueryWrapper<>();
            wrapper.orderByAsc("handled");
            wrapper.orderByDesc("date");
            IPage<ReportInfo> reportInfoIPage = reportInfoMapper.selectPage(new Page<>(page, size), wrapper);
            return ResultUtils.success(reportInfoIPage.getRecords());
        }catch (Exception e){
            return ResultUtils.error(-1,"error");
        }
    }

    @Override
    public RestResult adminPassReport(Long id) {
        try {
            ReportInfo reportInfo = reportInfoMapper.selectById(id);


            if (reportInfo.getHandled()==1){
                return ResultUtils.error(0,"请勿重复操作");
            }
            reportInfo.setHandled(1);
            reportInfoMapper.updateById(reportInfo);
            illegalUserService.updateIllegalUserNumByUsername(reportInfo.getIllegalPeople());


            userMessageService.sendUsersMessage(reportInfo.getInformer(),"您举报的用户" + reportInfo.getIllegalPeople() + "已成功" ,0,reportInfo.getId());
            userMessageService.sendUsersMessage(reportInfo.getIllegalPeople(),"您被举报"+reportInfo.getType() + reportInfo.getMsg() + "已经成功受理 违法记录+1",0,0);
            return ResultUtils.success();
        }catch (Exception e){
            return ResultUtils.error(-1,"error");
        }
    }

    @Override
    public RestResult adminDeleteReport(List<Long> ids) {
        try {
            for (Long id:ids){
                ReportInfo reportInfo = reportInfoMapper.selectById(id);
                if (reportInfo!=null) {
                    reportInfoMapper.deleteById(id);

                    //未处理 直接删掉 就是不通过
                    if (reportInfo.getHandled() == 0) {
                        userMessageService.sendUsersMessage(reportInfo.getInformer(), "您举报的用户" + reportInfo.getIllegalPeople() + "失败 证据不充分", 0, reportInfo.getId());

                    }
                }
            }

            return ResultUtils.success();
        }catch (Exception e){
            return ResultUtils.error(-1,"error");
        }
    }

    @Override
    public Integer getUserReportInfoNum(String username) {
        QueryWrapper<ReportInfo> wrapper = new QueryWrapper<>();
        wrapper.eq("informer",username);
        wrapper.eq("handled",0);
        List<ReportInfo> reportInfos = reportInfoMapper.selectList(wrapper);
        return reportInfos.size();
    }
}
