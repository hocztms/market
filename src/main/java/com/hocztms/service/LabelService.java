package com.hocztms.service;

import com.hocztms.common.RestResult;
import com.hocztms.entity.Label;

import java.util.List;

public interface LabelService {
    RestResult getGoodsAllLabelByFid(Long id);

    Integer deleteLabel(long id);

    Integer updateLabel(Label label);

    Integer insertLabel(Label label);

    List<Label> findLabelByFid(Long id);

    Label findLabelById(Long id);

    Label findLabel(Label label);
}
