package com.hocztms.utils;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class WebUtils {

    //解析 Body里面Id
    public List<Long> resolvIds(String ids){
        List <Long> longList = new ArrayList<>();
        String[] strIds = ids.split(",");
        System.out.println(strIds.toString());
        System.out.println("ok");
        for (String str:strIds){
            System.out.println(str);
            longList.add(Long.parseLong(str));
        }
        return longList;
    }

}
