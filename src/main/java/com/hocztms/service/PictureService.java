package com.hocztms.service;

import com.hocztms.common.RestResult;
import com.hocztms.entity.Picture;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface PictureService {

    RestResult insertPictureByUpload(Long goodsId, String username, int tag, MultipartFile file);

    RestResult deleteGoodsPictureByIds(List<Long>ids , String username);

    List<Picture> findPictureByGoodsId(Long goodsId);

    RestResult getGoodsMainPicture(Long goodsId);

    Integer insertPicture(Picture picture);

    Integer deleteGoodsPictureByGoodsId(Long goodsId);

    Picture findGoodsMainPicture(Long goodsId);
}
