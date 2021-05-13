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
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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
                return new RestResult(0,"您已经收藏请勿重新收藏",null);
            }

            Goods goods = goodsService.findGoodsByGoodsId(goodsId);
            if (goods==null){
                return new RestResult(0,"商品不存在",null);
            }

            Collection collection = new Collection(0,username,goodsId,goods.getMsg(),goods.getStatus());
            collectionMapper.insert(collection);
            return new RestResult(1,"操作成功",null);
        }catch (Exception e){
            return new RestResult(0,"操作失败",null);
        }
    }

    @Override
    public RestResult findUserCollectionByUsername(long page,long size,String username) {
        try {
            QueryWrapper <Collection> wrapper = new QueryWrapper<>();
            wrapper.eq("username",username);
            IPage<Collection> collectionIPage = collectionMapper.selectPage(new Page<>(page, size), wrapper);
            return new RestResult(1,"操作成功",collectionIPage.getRecords());
        }catch (Exception e){
            return new RestResult(0,"操作失败",null);
        }
    }

    @Override
    public RestResult deleteUserCollectionByIds(List<Long> ids, String username) {
        try {
            RestResult result = new RestResult(1,"操作成功",null);

            for (Long id:ids){
                if (collectionMapper.selectById(id)==null){
                    result.put("error",id + "不存在...");
                    log.warn(username + "正在执行非法操作.....");
                }
                else if (!collectionMapper.selectById(id).getUsername().equals(username)){
                    result.put("error",id + "无权限...");
                    log.warn(username + "正在执行非法操作.....");
                }
                else {
                    collectionMapper.deleteById(id);
                }
            }
            return result;
        }catch (Exception e){
            return new RestResult(0,"操作失败",null);
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
