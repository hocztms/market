package com.hocztms.service.Impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hocztms.common.RestResult;
import com.hocztms.entity.Goods;
import com.hocztms.mapper.GoodsMapper;
import com.hocztms.service.GoodsService;
import com.hocztms.service.IndexGoodsService;
import com.hocztms.utils.ResultUtils;
import com.hocztms.vo.OrderBy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class IndexGoodsServiceImpl implements IndexGoodsService {

    @Autowired
    private GoodsService goodsService;

    @Autowired
    private GoodsMapper goodsMapper;
    /*
    获取全部商品
     */
    @Override
    public RestResult indexGetGoods(long page,long size,int mode) {
        try{
            if (mode==0){
                List<Goods> goodsList = goodsMapper.findGoods("date","desc",new Page(page,size));
                if (goodsList.isEmpty()){
                    return ResultUtils.success("没有了",goodsList);
                }
                return ResultUtils.success("为你查找到以下商品",goodsList);
            }
            else {
                OrderBy orderBy = goodsService.setOrderByByMode(mode);
                List<Goods> goodsList = goodsMapper.findGoods(orderBy.getOrderBy(),orderBy.getBy(),new Page(page,size));
                if (goodsList.isEmpty()) {
                    return ResultUtils.success("没有了", goodsList);
                }
                return ResultUtils.success("为你查找到以下商品", goodsList);
            }
        }catch (Exception e){
            return ResultUtils.error(-1,"error");
        }
    }
    /*
    标签搜索
     */
    @Override
    public RestResult indexGetGoodsByLabel(long page, long size,long labelId,int mode) {
        try {
            if (mode==0){
                List<Goods> goodsList = goodsMapper.findGoodsByLabel(labelId,"date","desc",new Page(page,size));
                if (goodsList.isEmpty()){
                    return ResultUtils.success("没有了",goodsList);
                }
                return ResultUtils.success("为你查找到以下商品",goodsList);

            }

            else {
                OrderBy orderBy = goodsService.setOrderByByMode(mode);
                List<Goods> goodsList = goodsMapper.findGoodsByLabel(labelId,orderBy.getOrderBy(),orderBy.getBy(),new Page(page,size));
                if (goodsList.isEmpty()) {
                    return ResultUtils.success("没有了", goodsList);
                }
                return ResultUtils.success("为你查找到以下商品", goodsList);
            }
        }catch (Exception e){
            return ResultUtils.error(-1,"error");
        }
    }

    @Override
    public RestResult indexGetGoodsByKeyword(long page, long size, String keyword,int mode) {
        try {

            //已匹配度优先方案
            if (mode==0){
                return indexGetGoodsByKeywordInModeZero(page,size,keyword);
            }


            OrderBy orderBy = goodsService.setOrderByByMode(mode);
            List<Goods> checkGoods = goodsService.findGoodsPageByKeyword(0,size,keyword,orderBy.getOrderBy(),orderBy.getBy());

            //关键词搜索不到应显示相关商品
            if (checkGoods.isEmpty()){
                List<Goods> goodsByKeyWord = goodsMapper.findGoodsByKeyWordByOrderBy(keyword,orderBy.getOrderBy(),orderBy.getBy(),new Page(page, size));
                if (goodsByKeyWord.isEmpty()){
                    return ResultUtils.success("没有了",goodsByKeyWord);
                }
                return ResultUtils.success("抱歉没有查找到,但为您查找到以下相关商品",goodsByKeyWord);
            }

            List<Goods> goodsList = goodsService.findGoodsPageByKeyword(page,size,keyword,orderBy.getOrderBy(),orderBy.getBy());
            if (goodsList.isEmpty()){
                return ResultUtils.success("没有了",goodsList);
            }
            return ResultUtils.success("为你查找到以下商品",goodsList);
        }catch (Exception e){
            return ResultUtils.error(-1,"error");
        }
    }

    @Override
    public RestResult indexGetGoodsByKeywordInModeZero(long page, long size, String keyword) {
        List<Goods> checkGoods = goodsService.findGoodsPageByKeyword(page,size,keyword,"date","desc");

        //关键词搜索不到应显示相关商品
        if (checkGoods.isEmpty()){
            List<Goods> goodsByKeyWord = goodsMapper.findGoodsByKeyWord(keyword, new Page(page, size));
            if (goodsByKeyWord.isEmpty()){
                return ResultUtils.success("没有了",goodsByKeyWord);
            }
            return ResultUtils.success("为您查找到以下相关商品",goodsByKeyWord);
        }
        List<Goods> goodsList = goodsMapper.findGoodsByKeyWordByOrderBy(keyword,"date","desc",new Page(page,size));
        if (goodsList.isEmpty()){
            return ResultUtils.success("没有了",goodsList);
        }
        return ResultUtils.success("为你查找到以下商品",goodsList);
    }
}
