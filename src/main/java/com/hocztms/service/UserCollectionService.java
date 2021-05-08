package com.hocztms.service;

import com.hocztms.common.RestResult;
import com.hocztms.entity.Collection;

import java.util.List;

public interface UserCollectionService {

    RestResult userCollectGoods(Long goodsId, String username);

    RestResult findUserCollectionByUsername(long page,long size,String username);

    RestResult deleteUserCollectionByIds(List<Long> ids,String username);

    Collection findCollectByGoodsIdAndUsername(Long goodsId,String username);
}
