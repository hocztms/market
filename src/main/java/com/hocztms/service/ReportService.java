package com.hocztms.service;

import com.hocztms.common.RestResult;
import com.hocztms.vo.ReportVo;

import java.util.List;

public interface ReportService {

    RestResult userReportIllegalPeople(ReportVo reportVo,String username);

    RestResult adminGetReportRecords(Long page,Long size);

    RestResult adminPassReport(Long id);

    RestResult adminDeleteReport(List<Long> ids);

    Integer getUserReportInfoNum(String username);
}
