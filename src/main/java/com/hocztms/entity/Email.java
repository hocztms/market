package com.hocztms.entity;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Email {
    private String receiver;
    private String address;
    private String theme;
    private String msg;
    private Date date;
    private String secret;
}
