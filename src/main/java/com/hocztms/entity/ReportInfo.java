package com.hocztms.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "tb_reportInfo")
public class ReportInfo {
    @TableId(value = "id",type = IdType.AUTO)
    long id;
    String type;
    String msg;
    String illegalPeople;
    String informer;
    Date date;
    int handled;
}
