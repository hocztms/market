package com.hocztms.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

//已经测试
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RestResult {
    private int code;
    private String msg;
    private Object data;

    public void put(String key, Object data) {
        if (this.data == null) {
            this.data = new HashMap<String, Object>();
        }
        ((Map) this.data).put(key, data);
    }

}