package com.hocztms.utils;

import com.hankcs.hanlp.seg.common.Term;
import com.hankcs.hanlp.tokenizer.StandardTokenizer;
import com.hocztms.entity.Goods;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class GoodsUtils {

    //获取正则字符串
    public String getMsgRegex(String msg){

        //hanlp   分词器
        List<Term> termList = StandardTokenizer.segment(msg);

        // 拼接正则字符串
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < termList.size(); i++) {
            String word = termList.get(i).word;
            if (i != termList.size() - 1) {
                sb.append(word).append("|");
            } else {
                sb.append(word);
            }
        }
        return sb.toString();
    }

    public boolean isEmpty(Goods goods) {
        return goods.getMsg() == null || goods.getPrice() == 0 || goods.getLevel() == 0;
    }

    public boolean checkOrderBy(String orderBy){
        return "date".equals(orderBy) || "level".equals(orderBy) || "price".equals(orderBy);
    }
}
