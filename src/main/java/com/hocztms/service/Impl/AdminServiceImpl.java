package com.hocztms.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hocztms.common.RestResult;
import com.hocztms.entity.*;
import com.hocztms.mapper.IllegalMapper;
import com.hocztms.service.*;
import com.hocztms.utils.EamilUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class AdminServiceImpl implements AdminService {

    @Autowired
    private EmailService emailService;

    @Autowired
    private UserService userService;

    @Autowired
    private GoodsService goodsService;

    @Autowired
    private IllegalMapper illegalMapper;

    @Autowired
    private IllegalUserService illegalUserService;

    @Autowired
    private LabelService labelService;

    @Autowired
    private UserMessageService userMessageService;

    @Autowired
    private EamilUtils eamilUtils;



    /*
    管理获取未审核商品 orderBy为排序基准 model 1为升序 2为降序
     */
    @Override
    public RestResult adminGetGoods(long page, long size) {
        try{
            List<Goods> goodsPage = goodsService.findGoodsPageByAdmin(page, size);

            if (goodsPage.isEmpty()){
                return new RestResult(1,"没有了",goodsPage);
            }
            return new RestResult(1,"为您查找到以下未审核商品",goodsPage);

        }catch (Exception e){
            System.out.println(e.getMessage());
            return new RestResult(0,"系统错误 请联系管理员",null);
        }
    }

    @Override
    public RestResult adminDeleteGoods(Long goodId) {
        try {
            Goods goods = goodsService.findGoodsByGoodsId(goodId);
            Users users = userService.findUsersByUsername(goods.getSeller());
            if (goods==null){
                return new RestResult(0,"商品不存在",null);
            }

            if (goods.getTag()==-1||goods.getTag()==1){
                return new RestResult(0,"请勿重复操作",null);
            }

            if (users == null){
                return new RestResult(0,"用户不存在",null);
            }

            //更新非法次数
            illegalUserService.updateIllegalUserNumByUsername(users.getUsername());

            goodsService.updateGoodsTag(goodId,-1);
            emailService.sendCheckGoodsEmail(goodId,-1);

            userMessageService.sendUsersMessage(users.getUsername(),"您的商品id为" + goods.getId() + "  " + goods.getMsg() + "审核不通过.....",0,goods.getId());
            return new RestResult(1,"操作成功",null);
        }catch (Exception e){
            return new RestResult(0,e.getMessage(),null);
        }
    }

    @Override
    public RestResult adminPassGoods(Long goodId) {
        try {
            Goods goods = goodsService.findGoodsByGoodsId(goodId);
            if (goods==null){
                return new RestResult(0,"商品不存在",null);
            }

            if ((goods.getTag()==-1||goods.getTag()==1)){
                return new RestResult(0,"请勿重复操作",null);
            }

            goodsService.updateGoodsTag(goodId,1);
            emailService.sendCheckGoodsEmail(goodId,1);

            userMessageService.sendUsersMessage(goods.getSeller(),"您的商品id为" + goods.getId() + "  " + goods.getMsg() + "审核不通过,违法记录+1.....",0,goods.getId());

        }catch (Exception e){
            return new RestResult(0,e.getMessage(),null);
        }
        return new RestResult(1,"审核通过成功",null);
    }

    @Override
    public RestResult adminGetIllegalUser(long page,long size) {
        try {
            QueryWrapper<Illegal> wrapper = new QueryWrapper<>();
            wrapper.orderByDesc("num");
            IPage<Illegal> illegalIPage = illegalMapper.selectPage(new Page<>(page, size), wrapper);
            List<Illegal> illegalUsers = illegalIPage.getRecords();

            if (illegalUsers.isEmpty()){
                return new RestResult(1,"没有了",illegalUsers);
            }
            return new RestResult(1,"为您查找到以下用户",illegalUsers);
        }catch (Exception e){
            System.out.println(e.getMessage());
            return new RestResult(0,"操作失败",null);
        }
    }

    @Override
    public RestResult adminGetIllegalUserByUsername(String username) {
       try {
           QueryWrapper<Illegal> wrapper = new QueryWrapper<>();
           wrapper.eq("username",username);
           Illegal illegal = illegalMapper.selectOne(wrapper);
           if (illegal==null){
               return new RestResult(0,"用户不存在",null);
           }
           return new RestResult(1,"操作成功",illegal);
       }catch (Exception e){
           return new RestResult(0,"操作失败",null);
       }
    }

    @Override
    public RestResult adminFreezeUser(String username) {
        try {
            Users users = userService.findUsersByUsername(username);
            if(users.getStatus()==0){
                return new RestResult(0,"请勿重新操作",null);
            }
            if(userService.updateUserStatusByUsername(username,0)==0||
                    goodsService.adminFreezeUserGoods(username)==0){
                return new RestResult(0,"操作失败",null);
            }

            Email email = new Email(users.getUsername(),users.getEmail(),"通知","您的账号已被冻结",new Date(),null);
            eamilUtils.sendEmail(email);
            return new RestResult(1,"冻结成功",null);
        }catch (Exception e){
            return new RestResult(0,"失败",null);
        }
    }

    @Override
    public RestResult adminUnFreezeUser(String username) {
        try {
            Users users = userService.findUsersByUsername(username);
            if(users.getStatus()==1){
                return new RestResult(0,"请勿重新操作",null);
            }

            if (userService.updateUserStatusByUsername(username,1)==0||
                    goodsService.adminUnFreezeUserGoods(username)==0){
                return new RestResult(0,"解除冻结失败",null);
            }

            Email email = new Email(users.getUsername(),users.getEmail(),"通知","您的账号已被解除冻结",new Date(),null);
            eamilUtils.sendEmail(email);
            return new RestResult(1,"解除冻结成功",null);
        }catch (Exception e){
            return new RestResult(0,"失败",null);
        }
    }

    @Override
    public RestResult adminDeleteLabelById(Long id) {
        try {

            //如果存在第二级标签 则删除 标签只有2级
            List<Label> labelByFid = labelService.findLabelByFid(id);
            if (!labelByFid.isEmpty()){
                for (Label label:labelByFid){
                    labelService.deleteLabel(label.getId());
                }
            }

            if (labelService.deleteLabel(id)==0){
                return new RestResult(0,"操作失败",null);
            }
            return new RestResult(1,"操作成功",null);
        }catch (Exception e){
            return new RestResult(0,"操作失败",null);
        }
    }

    @Override
    public RestResult adminUpdateLabel(Label label) {
        try {
            if (labelService.findLabel(label)!=null){
                return new RestResult(0,"标签已存在",null);
            }
            if (labelService.updateLabel(label)==0){
                return new RestResult(0,"操作失败",null);
            }
            return new RestResult(1,"操作成功",null);
        }catch (Exception e){
            return new RestResult(0,"操作失败",null);
        }
    }

    @Override
    public RestResult adminInsertLabel(Label label) {
        try {
            if (label.getFid()!=0&&labelService.findLabelByFid(label.getFid())==null){
                return new RestResult(0,"非法操作",null);
            }
            if (labelService.findLabel(label)!=null){
                return new RestResult(0,"标签已存在",null);
            }

            if (labelService.insertLabel(label)==0){
                return new RestResult(0,"操作失败",null);
            }
            return new RestResult(1,"操作成功",null);
        }catch (Exception e){
            return new RestResult(0,"操作失败",null);
        }
    }

}
