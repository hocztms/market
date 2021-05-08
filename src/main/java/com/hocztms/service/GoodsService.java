package com.hocztms.service;

import com.hocztms.common.RestResult;
import com.hocztms.entity.Goods;
import com.hocztms.entity.GoodsLabel;
import com.hocztms.entity.Label;
import com.hocztms.entity.OrderForm;
import com.hocztms.vo.GoodsVo;
import com.hocztms.vo.OrderBy;
import com.hocztms.vo.OrderFormVo;

import java.util.List;

public interface GoodsService {

    RestResult userCreateGoods(GoodsVo goodsVo, String username);

    RestResult getGoodsDetails(Long goodsId);

    RestResult deleteUserGoodsByIds(List<Long> ids, String username);

    RestResult updateUserGoods(GoodsVo goodsVo, String username);

    RestResult findGoodsLabelById(Long GoodsId);

    RestResult deleteGoodsLabelById(Long id, String username);

    GoodsLabel findGoodsLabel(GoodsLabel goodsLabel);

    Integer createGoodsLabel(Long labelId, Long goodsId);

    void updateGoodsTag(Long goodsId, int tag);

    Goods findGoodsByGoodsId(Long id);

    boolean checkOrderBy(String orderBy);

    boolean isEmpty(Goods goods);

    List<Goods> selectListByUsername(String username);

    List<Goods> findGoodsPage(long page, long size, String orderBy, int model);

    List<Goods> findGoodsPageByKeyword(long page, long size, String keyword, String orderBy, String model);

    Integer deleteGoodsById(Long id);

    Integer updateOptimisticLockGoods(Long id);

    Integer updateGoodsStatusById(Long id,int status);

    Integer adminFreezeUserGoods(String username);

    Integer adminUnFreezeUserGoods(String username);

    OrderBy setOrderByByMode(int mode);

    Goods initializeGoods(GoodsVo goodsVo, String username);

}
