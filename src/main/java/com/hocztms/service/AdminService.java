package com.hocztms.service;

import com.hocztms.common.RestResult;
import com.hocztms.entity.Label;

public interface AdminService {

    RestResult adminGetGoods(long page, long size, String orderBy, int model);

    RestResult adminDeleteGoods(Long goodId);

    RestResult adminPassGoods(Long goodId);

    RestResult adminGetIllegalUser(long page, long size);

    RestResult adminFreezeUser(String username);

    RestResult adminUnFreezeUser(String username);

    RestResult adminDeleteLabelById(Long id);

    RestResult adminUpdateLabel(Label label);

    RestResult adminInsertLabel(Label label);

}
