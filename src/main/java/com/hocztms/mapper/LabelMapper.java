package com.hocztms.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hocztms.entity.Goods;
import com.hocztms.entity.Label;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface LabelMapper extends BaseMapper<Label> {

}
