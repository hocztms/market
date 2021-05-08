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
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < termList.size(); i++) {
            String word = termList.get(i).word;
            if (i != termList.size() - 1) {
                sb.append(word + "|");
            } else {
                sb.append(word);
            }
        }
        return sb.toString();
    }

    public boolean isEmpty(Goods goods) {
        if (goods.getMsg()==null||goods.getPrice()==0||goods.getLevel()==0){
            return true;
        }
        return false;
    }

    public boolean checkOrderBy(String orderBy){
        if (orderBy.equals("date")||orderBy.equals("level")||orderBy.equals("price")){
            return true;
        }
        else {
            return false;
        }
    }
}
