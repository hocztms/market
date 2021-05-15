package com.hocztms.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hocztms.common.RestResult;
import com.hocztms.entity.Collection;
import com.hocztms.entity.Goods;
import com.hocztms.mapper.CollectionMapper;
import com.hocztms.service.GoodsService;
import com.hocztms.service.UserCollectionService;
import com.hocztms.utils.ResultUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class UserCollectionServiceImpl implements UserCollectionService {

    @Autowired
    private CollectionMapper collectionMapper;

    @Autowired
    private GoodsService goodsService;


    @Override
    public RestResult userCollectGoods(Long goodsId, String username) {
        try {
            if (findCollectByGoodsIdAndUsername(goodsId,username)!=null){
                return ResultUtils.error(0,"您已经收藏请勿重新收藏");
            }

            Goods goods = goodsService.findGoodsByGoodsId(goodsId);
            if (goods==null){
                return ResultUtils.error(0,"商品不存在");
            }

            Collection collection = new Collection(0,username,goodsId,goods.getMsg(),goods.getStatus());
            collectionMapper.insert(collection);
            return ResultUtils.success();
        }catch (Exception e){
            return ResultUtils.error(-1,"error");
        }
    }

    @Override
    public RestResult findUserCollectionByUsername(long page,long size,String username) {
        try {
            QueryWrapper <Collection> wrapper = new QueryWrapper<>();
            wrapper.eq("username",username);
            IPage<Collection> collectionIPage = collectionMapper.selectPage(new Page<>(page, size), wrapper);
            return ResultUtils.success(collectionIPage.getRecords());
        }catch (Exception e){
            return ResultUtils.error(-1,"error");
        }
    }

    @Override
    public RestResult deleteUserCollectionByIds(List<Long> ids, String username) {
        try {
            Map<Integer,String> errors = new HashMap<>();
            int i=1;

            for (Long id:ids){
                if (collectionMapper.selectById(id)==null){
                    errors.put(i++,id + "不存在...");
                    log.warn(username + "正在执行非法操作.....");
                }
                else if (!collectionMapper.selectById(id).getUsername().equals(username)){
                    errors.put(i++,id + "无权限...");
                    log.warn(username + "正在执行非法操作.....");
                }
                else {
                    collectionMapper.deleteById(id);
                }
            }
            if(!errors.isEmpty()){
                RestResult result = new RestResult(1,"部分失败",null);
                result.put("errors",errors);
                return result;
            }
            return ResultUtils.success();
        }catch (Exception e){
            return ResultUtils.error(-1,"error");
        }
    }

    @Override
    public Collection findCollectByGoodsIdAndUsername(Long goodsId, String username) {
        QueryWrapper<Collection> wrapper = new QueryWrapper<>();
        wrapper.eq("goods_id",goodsId);
        wrapper.eq("username",username);
        return collectionMapper.selectOne(wrapper);
    }
}
