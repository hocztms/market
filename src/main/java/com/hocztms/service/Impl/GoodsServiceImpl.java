package com.hocztms.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hocztms.common.RestResult;
import com.hocztms.entity.*;
import com.hocztms.mapper.GoodsLabelMapper;
import com.hocztms.mapper.GoodsMapper;
import com.hocztms.mapper.LabelMapper;
import com.hocztms.service.*;
import com.hocztms.utils.GoodsUtils;
import com.hocztms.utils.ResultUtils;
import com.hocztms.vo.GoodsLabelVo;
import com.hocztms.service.GoodsService;
import com.hocztms.vo.GoodsVo;
import com.hocztms.vo.OrderBy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class GoodsServiceImpl implements GoodsService {

    @Autowired
    private GoodsUtils goodsUtils;

    @Autowired
    private GoodsMapper goodsMapper;

    @Autowired
    private PictureService pictureService;

    @Autowired
    private UserService userService;

    @Autowired
    private LabelMapper labelMapper;

    @Autowired
    private GoodsLabelMapper goodsLabelMapper;

    @Autowired
    private UserMessageService userMessageService;

    @Autowired
    private OrderFormService orderFormService;


    @Override
    public RestResult userCreateGoods(GoodsVo goodsVo, String username) {
        try {

            Goods goods = initializeGoods(goodsVo, username);
            if (goodsMapper.insert(goods) == 0) {
                return ResultUtils.error(0, "操作失败");
            }

            RestResult result = new RestResult(1, "操作成功", null);
            result.put("goodsId", goods.getId());

            for (Long id : goodsVo.getLabelIds()) {
                createGoodsLabel(id, goods.getId());
            }

            userMessageService.sendUsersMessage(username, "商品创建成功,请等待管理员审核。。。。。", 0, goods.getId());

            userMessageService.sendAdminGoodsMessage();
            return result;
        } catch (Exception e) {
            return ResultUtils.error(-1,"error");
        }
    }

    @Override
    public RestResult getGoodsDetails(Long id) {
        try {
            QueryWrapper<Goods> wrapper = new QueryWrapper<>();
            wrapper.eq("id", id);
            Goods goods = goodsMapper.selectOne(wrapper);

            if (goods == null) {
                return ResultUtils.error(0, "商品不存在");
            }
            RestResult result = new RestResult(1, "成功", null);
            List<Picture> goodsPicture = pictureService.findPictureByGoodsId(id);
            Users users = userService.findUsersByUsername(goods.getSeller());
            RestResult goodsLabelById = findGoodsLabelById(id);
            result.put("goods", goods);
            result.put("pictures", goodsPicture);
            result.put("sellerEmail", users.getEmail());
            result.put("sellerPhone", users.getPhone());
            result.put("goodsLabels", goodsLabelById.getData());
            return result;
        } catch (Exception e) {
            return ResultUtils.error(-1,"error");
        }
    }

    @Override
    public RestResult deleteUserGoodsByIds(List<Long> ids, String username) {
        try {
            Map<Integer,String> errors = new HashMap<>();
            int i=1;

            for (Long id : ids) {
                Goods goods = findGoodsByGoodsId(id);
                OrderForm orderForm = orderFormService.findOrderFormById(id);
                //只有未订单的商品是可以删除的 如果有订单且未完成 就不能删除 审核不通过未审核商品审核成功商品都能删除
                if (goods == null) {
                    errors.put(i++, id + " 商品不存在");
                }
                else if (!goods.getSeller().equals(username)) {
                    errors.put(i++, id + " 违法操作,无权限");
                } else if (orderForm != null && orderForm.getTag() == 0) {
                    errors.put(i++, id + " 订单未完成");
                } else {
                    deleteGoodsById(id);
                }
            }

            if(!errors.isEmpty()){
                RestResult result = new RestResult(1,"部分失败",null);
                result.put("errors",errors);
                return result;
            }
            return ResultUtils.success();
        } catch (Exception e) {
            return ResultUtils.error(-1,"error");
        }
    }

    @Override
    public RestResult updateUserGoods(GoodsVo goodsVo, String username) {
        try {
            Goods goods = findGoodsByGoodsId(goodsVo.getGoodsId());

            //售出的商品就不能再更改信息 也不需要更改信息 而其他审核不通过 审核通过 未审核都能更改 且会更改状态 不需要单独去写 审核不通过重发布的接口
            if (goods.getStatus()==0){
                return ResultUtils.error(0,"商品已售出");
            }

            //鉴权
            if (!goods.getSeller().equals(username)) {
                return ResultUtils.error(0, "无权限");
            }

            //创建没有的商品标签
            for (Long id : goodsVo.getLabelIds()) {
                createGoodsLabel(id, goods.getId());
            }

            //更新商品信息
            goods.setMsg(goodsVo.getMsg());
            goods.setPrice(goodsVo.getPrice());
            goods.setLevel(goodsVo.getLevel());
            //改变为未审核状态
            goods.setTag(0);

            goodsMapper.updateById(goods);

            userMessageService.sendUsersMessage(username, "商品更新成功,请等待管理员审核。。。。。", 0, goods.getId());
            userMessageService.sendAdminGoodsMessage();
            return ResultUtils.success();
        } catch (Exception e) {
            return ResultUtils.error(-1,"error");
        }
    }


    @Override
    public RestResult findGoodsLabelById(Long goodsId) {
        try {
            QueryWrapper<GoodsLabel> wrapper = new QueryWrapper<>();
            wrapper.eq("goods_id", goodsId);
            List<GoodsLabel> goodsLabels = goodsLabelMapper.selectList(wrapper);
            return ResultUtils.success(goodsLabels);
        } catch (Exception e) {
            return ResultUtils.error(-1,"error");
        }
    }

    @Override
    public RestResult addGoodsLabelById(GoodsLabelVo goodsLabelVo, String username) {
        try {
            Goods goodsByGoodsId = findGoodsByGoodsId(goodsLabelVo.getGoodsId());
            if (goodsByGoodsId.getStatus()==0){
                return ResultUtils.error(0,"商品已售出");
            }
            if (!username.equals(goodsByGoodsId.getSeller())) {
                return ResultUtils.error(0, "无权限");
            }

            if (!goodsLabelVo.getLabelIds().isEmpty()) {
                for (Long id : goodsLabelVo.getLabelIds()) {
                    createGoodsLabel(id, goodsLabelVo.getGoodsId());
                }
            }

            updateGoodsTag(goodsLabelVo.getGoodsId(),0);
            userMessageService.sendAdminGoodsMessage();
            userMessageService.sendUsersMessage(username,"商品图片上传成功,请等待管理员审核。。。。。",0,goodsLabelVo.getGoodsId());

            return ResultUtils.success();
        } catch (Exception e) {
            return ResultUtils.error(-1,"error");
        }
    }

    @Override
    public RestResult deleteGoodsLabelById(Long id, String username) {
        try {
            GoodsLabel label = goodsLabelMapper.selectById(id);
            Goods goods = findGoodsByGoodsId(label.getGoodsId());
            if (!goods.getSeller().equals(username)) {
                return ResultUtils.error(0, "无权限");
            }

            if (goodsLabelMapper.deleteById(label.getId()) == 0) {
                return ResultUtils.error(0, "操作失败");
            }

            return ResultUtils.success();
        } catch (Exception e) {
            return ResultUtils.error(-1,"error");
        }
    }

    @Override
    public GoodsLabel findGoodsLabel(GoodsLabel goodsLabel) {
        QueryWrapper<GoodsLabel> wrapper = new QueryWrapper<>();
        wrapper.eq("goods_id", goodsLabel.getGoodsId());
        wrapper.eq("label_id", goodsLabel.getLabelId());
        return goodsLabelMapper.selectOne(wrapper);
    }

    @Override
    public Integer createGoodsLabel(Long labelId, Long goodsId) {
        Label label = labelMapper.selectById(labelId);

        if (label == null) {
            return 0;
        }

        long id = labelId;

        //创建上一级的商品
        while (id > 0) {
            label = labelMapper.selectById(id);
            GoodsLabel goodsLabel = new GoodsLabel(0, label.getId(), label.getName(), goodsId);
            if (findGoodsLabel(goodsLabel) == null) {
                goodsLabelMapper.insert(goodsLabel);
            }
            id = label.getFid();
        }
        return 1;
    }


    @Override
    public void updateGoodsTag(Long goodsId, int tag) {
        QueryWrapper<Goods> wrapper = new QueryWrapper<>();
        wrapper.eq("id", goodsId);
        Goods goods = findGoodsByGoodsId(goodsId);
        goods.setTag(tag);
        goodsMapper.update(goods, wrapper);
    }

    @Override
    public Goods findGoodsByGoodsId(Long id) {
        QueryWrapper<Goods> wrapper = new QueryWrapper<>();
        wrapper.eq("id", id);
        return goodsMapper.selectOne(wrapper);
    }

    /*
    检查orderBy规范
     */
    @Override
    public boolean checkOrderBy(String orderBy) {
        return goodsUtils.checkOrderBy(orderBy);
    }


    @Override
    public List<Goods> selectListByUsername(String username) {
        QueryWrapper<Goods> wrapper = new QueryWrapper<>();
        wrapper.eq("seller", username);
        return goodsMapper.selectList(wrapper);
    }

    @Override
    public List<Goods> findGoodsPage(long page, long size, String orderBy, int model) {
        QueryWrapper<Goods> wrapper = new QueryWrapper<>();
        wrapper.eq("tag", 1);
        wrapper.eq("status", 1);
        if (model == 1) {
            wrapper.orderByAsc(orderBy);
        } else {
            wrapper.orderByDesc(orderBy);
        }

        IPage<Goods> goodsIPage = goodsMapper.selectPage(new Page<>(page, size), wrapper);
        return goodsIPage.getRecords();
    }

    @Override
    public List<Goods> findGoodsPageByAdmin(long page, long size) {
        QueryWrapper<Goods> wrapper = new QueryWrapper<>();
        wrapper.eq("tag", 0);
        wrapper.orderByDesc("date");
        IPage<Goods> goodsIPage = goodsMapper.selectPage(new Page<>(page, size), wrapper);
        return goodsIPage.getRecords();
    }

    @Override
    public List<Goods> findGoodsPageByKeyword(long page, long size, String keyword, String orderBy, String model) {
        QueryWrapper<Goods> wrapper = new QueryWrapper<>();
        wrapper.like("msg", "%" + keyword + "%");
        wrapper.eq("tag", 1);
        wrapper.eq("status", 1);

        if ("asc".equals(model)) {
            wrapper.orderByAsc(orderBy);
        } else {
            wrapper.orderByDesc(orderBy);
        }
        IPage<Goods> goodsIPage = goodsMapper.selectPage(new Page<>(page, size), wrapper);
        return goodsIPage.getRecords();
    }


    @Override
    public Integer deleteGoodsById(Long id) {
        if (pictureService.deleteGoodsPictureByGoodsId(id) == 0) {
            return 0;
        }
        return goodsMapper.deleteById(id);
    }

    @Override
    public Integer updateOptimisticLockGoods(Long id) {
        Goods goods = goodsMapper.selectById(id);
        QueryWrapper<Goods> wrapper = new QueryWrapper<>();
        wrapper.eq("id",goods.getId());
        //其实在利用这个status 这个属性在某种程度上已经实现了 自行乐观锁
        wrapper.eq("status",1);
        goods.setStatus(0);
        return goodsMapper.update(goods,wrapper);
    }

    @Override
    public Integer updateGoodsStatusById(Long id, int status) {
        Goods goods = findGoodsByGoodsId(id);
        goods.setStatus(status);
        return goodsMapper.updateById(goods);
    }

    @Override
    public Integer adminFreezeUserGoods(String username) {
        try {
            QueryWrapper<Goods> wrapper = new QueryWrapper<>();
            wrapper.eq("seller", username);


            //未售出的商品全部冻结
            wrapper.eq("status", "1");
            List<Goods> goods = goodsMapper.selectList(wrapper);
            if (goods.isEmpty()) {
                return 1;
            }

            for (Goods good : goods) {
                good.setStatus(-1);
                goodsMapper.updateById(good);
            }
            return 1;
        } catch (Exception e) {
            return 0;
        }
    }

    @Override
    public Integer adminUnFreezeUserGoods(String username) {
        try {
            QueryWrapper<Goods> wrapper = new QueryWrapper<>();
            wrapper.eq("seller", username);
            wrapper.eq("status", "-1");
            List<Goods> goods = goodsMapper.selectList(wrapper);
            if (goods.isEmpty()) {
                return 1;
            }

            for (Goods good : goods) {
                good.setStatus(0);
                goodsMapper.updateById(good);
            }
            return 1;
        } catch (Exception e) {
            return 0;
        }
    }

    @Override
    public List<Goods> findUserNormalGoodsByUsername(long page, long size, String username) {
        QueryWrapper<Goods> wrapper = new QueryWrapper<>();
        wrapper.ne("tag",-1);
        wrapper.eq("seller",username);
        wrapper.orderByDesc("tag");
        wrapper.orderByDesc("date");
        return goodsMapper.selectPage(new Page<>(page,size),wrapper).getRecords();
    }

    @Override
    public List<Goods> findUserIllegalGoodsByUsername(long page, long size, String username) {
        QueryWrapper<Goods> wrapper = new QueryWrapper<>();
        wrapper.eq("tag",-1);
        wrapper.eq("seller",username);
        wrapper.orderByDesc("date");
        return goodsMapper.selectPage(new Page<>(page,size),wrapper).getRecords();
    }

    @Override
    public OrderBy setOrderByByMode(int mode) {
        OrderBy orderBy = new OrderBy();
        switch (mode) {
            case 1:
                orderBy.setOrderBy("price");
                orderBy.setBy("asc");
                break;
            case 2:
                orderBy.setOrderBy("price");
                orderBy.setBy("desc");
                break;
            case 3:
                orderBy.setOrderBy("level");
                orderBy.setBy("asc");
                break;
            case 4:
                orderBy.setOrderBy("level");
                orderBy.setBy("desc");
                break;
            default:
                throw new RuntimeException("非法操作");
        }
        return orderBy;
    }

    @Override
    public Goods initializeGoods(GoodsVo goodsVo, String username) {
        return new Goods(0, goodsVo.getMsg(), goodsVo.getPrice(), username, goodsVo.getLevel(), new Date(), 0, 1, 1);
    }
}