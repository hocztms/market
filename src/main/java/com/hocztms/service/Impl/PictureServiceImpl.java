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
import com.hocztms.utils.ResultUtils;
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
            return ResultUtils.error(0,"商品不存在");
        }
        if (!goods.getSeller().equals(username)){
            return ResultUtils.error(0,"无权限");
        }

        if (file.isEmpty()) {
            return ResultUtils.error(0,"文件不能为空");
        }


        // 获取文件名
        String fileName = file.getOriginalFilename();
        // 获取文件的后缀名
        String suffixName = fileName.substring(fileName.lastIndexOf("."));

        //检查文件格式
        if(!fileUtils.checkPictureSuffixName(suffixName)){
            return ResultUtils.error(0,"文件格式不正确");
        }

        //判断文件大小
        if (fileUtils.checkFileSize(file.getSize(),2,"M")){
            return ResultUtils.error(0,"图片不能超过2M");
        }


        //设置唯一文件名
        String uuid = UUID.randomUUID().toString().replace("-", "");
        fileName = uuid + "_" + fileName;


        //上传文件并检查图片是否合法
        if (fileUtils.uploadPicture(fileName,file)==0) {
            return ResultUtils.error(0, "文件上传失败 非法图片");
        }

        //上传至数据库
        try {
            //如果已经有主封面也直接设置成当前为主封面

            Picture picture = new Picture(0,goodsId,username,new Date(),fileName,tag);
            insertPicture(picture);
        }catch (Exception e){
            log.warn(e.getMessage());
            return ResultUtils.error(-1,"error");
        }


        goodsService.updateGoodsTag(goodsId,0);
        userMessageService.sendAdminGoodsMessage();
        userMessageService.sendUsersMessage(username,"商品图片上传成功,请等待管理员审核。。。。。",0,goods.getId());
        return ResultUtils.success();
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
            return ResultUtils.success();
        }catch (Exception e){
            log.warn(e.getMessage());
            return ResultUtils.error(-1,"error");
        }
    }


    @Override
    public RestResult getGoodsMainPicture(Long goodsId) {
        try {
            System.out.println(goodsId);
            Goods good = goodsService.findGoodsByGoodsId(goodsId);
            if (good==null){
                return ResultUtils.error(0,"商品不存在");
            }
            Picture mainPicture = findGoodsMainPicture(goodsId);
            if(mainPicture==null){
                List<Picture> pictures = findPictureByGoodsId(goodsId);
                if (pictures.isEmpty()){
                    return ResultUtils.error(0,"商品图片不存在");
                }
                return ResultUtils.success("不存在主图随机找个图片代替",pictures.get(0).getPicturename());
            }

            return ResultUtils.success(mainPicture.getPicturename());
        }catch (Exception e){
            log.warn(e.getMessage());
            return ResultUtils.error(-1,"error");
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
        QueryWrapper<Picture> wrapper = new QueryWrapper<>();
        wrapper.eq("goods_id",goodsId);
        return pictureMapper.selectList(wrapper);
    }
}
