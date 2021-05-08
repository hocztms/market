package com.hocztms.vo;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
//用来封装socket 信息
public class SocketMessage {
    private int code;
    private Object message;
}
