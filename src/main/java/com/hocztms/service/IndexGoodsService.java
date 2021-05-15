package com.hocztms.service;

import com.hocztms.common.RestResult;

public interface IndexGoodsService {
    RestResult indexGetGoods(long page, long size,int mode);

    RestResult indexGetGoodsByLabel(long page,long size,long labelId,int mode);

    RestResult indexGetGoodsByKeyword(long page, long size, String keyword,int mode);

    RestResult indexGetGoodsByKeywordInModeZero(long page, long size, String keyword);

}
