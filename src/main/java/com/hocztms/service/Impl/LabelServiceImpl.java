package com.hocztms.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hocztms.common.RestResult;
import com.hocztms.entity.Label;
import com.hocztms.mapper.LabelMapper;
import com.hocztms.service.LabelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LabelServiceImpl implements LabelService {

    @Autowired
    private LabelMapper labelMapper;



    @Override
    public RestResult getGoodsAllLabelByFid(Long id) {
        try {
            List<Label> labelByFid = findLabelByFid(id);
            return new RestResult(1,"成功",labelByFid);
        }catch (Exception e){
            return new RestResult(0,"失败",null);
        }
    }

    @Override
    public Integer deleteLabel(long id) {
        return labelMapper.deleteById(id);
    }

    @Override
    public Integer updateLabel(Label label) {
        return labelMapper.updateById(label);
    }

    @Override
    public Integer insertLabel(Label label) {
        return labelMapper.insert(label);
    }

    @Override
    public List<Label> findLabelByFid(Long id) {
        QueryWrapper<Label> wrapper = new QueryWrapper<>();
        wrapper.eq("fid",id);
        return labelMapper.selectList(wrapper);
    }

    @Override
    public Label findLabelById(Long id) {
        return labelMapper.selectById(id);
    }


    @Override
    public Label findLabel(Label label) {
        QueryWrapper<Label> wrapper = new QueryWrapper<>();
        wrapper.eq("name",label.getName());
        wrapper.eq("fid",label.getFid());
        return labelMapper.selectOne(wrapper);
    }

}
