package com.hocztms.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hocztms.common.RestResult;
import com.hocztms.entity.Goods;
import com.hocztms.entity.Picture;
import com.hocztms.mapper.PictureMapper;
import com.hocztms.service.GoodsService;
import com.hocztms.service.PictureService;
import com.hocztms.service.UserMessageService;
import com.hocztms.utils.FileUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@Service
@Slf4j
public class PictureServiceImpl implements PictureService {
    @Autowired
    private FileUtils fileUtils;

    @Autowired
    private PictureMapper pictureMapper;

    @Autowired
    private GoodsService goodsService;

    @Autowired
    private UserMessageService userMessageService;


    @Override
    public RestResult insertPictureByUpload(Long goodsId, String username, int tag, MultipartFile file){
        //权限校验 goods的seller与jwt比对
        Goods goods = goodsService.findGoodsByGoodsId(goodsId);
        if (goods==null){
            return new RestResult(0,"商品不存在",null);
        }
        if (!goods.getSeller().equals(username)){
            return new RestResult(0,"无权限",null);
        }

        if (file.isEmpty()) {
            return new RestResult(0,"文件不能为空",null);
        }


        // 获取文件名
        String fileName = file.getOriginalFilename();
        // 获取文件的后缀名
        String suffixName = fileName.substring(fileName.lastIndexOf("."));

        //检查文件格式
        if(!fileUtils.checkPictureSuffixName(suffixName)){
            return new RestResult(0,"文件格式不正确",null);
        }

        //判断文件大小
        if (fileUtils.checkFileSize(file.getSize(),2,"M")){
            return new RestResult(0,"图片不能超过2M",null);
        }


        //设置唯一文件名
        String uuid = UUID.randomUUID().toString().replace("-", "");
        fileName = uuid + "_" + fileName;


        //上传文件并检查图片是否合法
        if (fileUtils.uploadPicture(fileName,file)==0) {
            return new RestResult(0, "文件上传失败", null);
        }

        //上传至数据库
        try {
            //如果已经有主封面也直接设置成当前为主封面

            Picture picture = new Picture(0,goodsId,username,new Date(),fileName,tag);
            insertPicture(picture);
        }catch (Exception e){
            log.warn(e.getMessage());
            return new RestResult(0,e.getMessage(),null);
        }


        goodsService.updateGoodsTag(goodsId,0);
        userMessageService.sendAdminGoodsMessage();
        userMessageService.sendUsersMessage(username,"商品图片上传成功,请等待管理员审核。。。。。",0,goods.getId());
        return new RestResult(1,"上传成功",null);
    }

    @Override
    public RestResult deleteGoodsPictureByIds(List<Long> ids, String username) {
        try {
            Map<Integer,String> errors = new HashMap<>();
            int i=1;
            for (Long id:ids){
                Picture picture = pictureMapper.selectById(id);
                if (picture==null){
                    errors.put(i++,id + " 不存在");
                }
                if (!picture.getUsername().equals(username)){
                    errors.put(i++,id + "无权限删除...");
                    log.warn(username + "正在执行违法操作....");
                }
                else {
                    fileUtils.deletePicture(picture.getPicturename());
                    pictureMapper.deleteById(picture.getId());
                }
            }

            if(!errors.isEmpty()){
                RestResult result = new RestResult(1,"部分失败",null);
                result.put("errors",errors);
                return result;
            }
            return new RestResult(1,"操作成功",null);
        }catch (Exception e){
            log.warn(e.getMessage());
            return new RestResult(0,"操作失败",null);
        }
    }


    @Override
    public RestResult getGoodsMainPicture(Long goodsId) {
        try {
            System.out.println(goodsId);
            Goods good = goodsService.findGoodsByGoodsId(goodsId);
            if (good==null){
                return new RestResult(0,"商品不存在",null);
            }
            Picture mainPicture = findGoodsMainPicture(goodsId);
            if(mainPicture==null){
                List<Picture> pictures = findPictureByGoodsId(goodsId);
                if (pictures.isEmpty()){
                    return new RestResult(0,"商品图片不存在",null);
                }
                return new RestResult(0,"不存在主图随机找个图片代替",pictures.get(0).getPicturename());
            }

            return new RestResult(1,"成功",mainPicture.getPicturename());
        }catch (Exception e){
            log.warn(e.getMessage());
            return new RestResult(0,"系统异常 请联系管理员",null);
        }
    }

    @Override
    public Integer insertPicture(Picture picture){
        if (picture.getTag()==1){
            Picture goodsMainPicture = findGoodsMainPicture(picture.getGoodsId());
            if (goodsMainPicture!=null){
                goodsMainPicture.setTag(0);
                pictureMapper.updateById(goodsMainPicture);
            }
        }
        pictureMapper.insert(picture);
        return 1;
    }

    @Override
    public Integer deleteGoodsPictureByGoodsId(Long goodsId) {
        QueryWrapper<Picture> wrapper = new QueryWrapper<>();
        wrapper.eq("goods_id", goodsId);
        List<Picture> pictures = pictureMapper.selectList(wrapper);
        if (pictures.isEmpty()){
            return 1;
        }
        for (Picture picture : pictures) {
            fileUtils.deletePicture(picture.getPicturename());
        }
        return pictureMapper.delete(wrapper);
    }

    @Override
    public Picture findGoodsMainPicture(Long goodsId) {
        QueryWrapper<Picture> wrapper = new QueryWrapper<>();
        wrapper.eq("tag",1);
        wrapper.eq("goods_id",goodsId);
        return pictureMapper.selectOne(wrapper);
    }

    @Override
    public List<Picture> findPictureByGoodsId(Long goodsId) {
        QueryWrapper wrapper = new QueryWrapper();
        wrapper.eq("goods_id",goodsId);
        return pictureMapper.selectList(wrapper);
    }
}
