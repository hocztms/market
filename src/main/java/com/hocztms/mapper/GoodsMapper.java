package com.hocztms.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.hocztms.entity.Goods;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;


@Mapper
public interface GoodsMapper extends BaseMapper<Goods> {

    @Select("select * from tb_goods where status = 1 and tag = 1 order by ${order} ${model}")
    List<Goods> findGoods(@Param("order") String order,@Param("model") String model,IPage page);

//    @Select("select * from tb_goods where msg regexp #{regex} and status = 1 and tag = 1 order by ${order} ${model} limit #{page},#{size}")
//    List<Goods> findByRegex(@Param("regex") String regex,@Param("page") long page,@Param("size") long size,@Param("order") String order,@Param("model") String model);

    @Select("SELECT * FROM tb_goods WHERE tag = 1 AND status = 1 AND MATCH (msg) AGAINST ( #{keyword} IN NATURAL LANGUAGE MODE)")
    List<Goods> findGoodsByKeyWord(@Param("keyword") String keyword,IPage iPage);

    @Select("SELECT * FROM tb_goods WHERE tag = 1 AND status = 1 AND MATCH (msg) AGAINST ( #{keyword} IN NATURAL LANGUAGE MODE) order by ${order} ${model}")
    List<Goods> findGoodsByKeyWordByOrderBy(@Param("keyword") String keyword,@Param("order") String order,@Param("model") String model,IPage iPage);

    @Select("select * from tb_goods,tb_goodsLabel where (tb_goods.id = tb_goodsLabel.goods_id and tb_goodsLabel.label_id = #{id} and tb_goods.status = 1 and tb_goods.tag = 1) order by ${order} ${model}")
    List<Goods> findGoodsByLabel(@Param("id") Long id,@Param("order") String order,@Param("model") String model, IPage iPage);

}
