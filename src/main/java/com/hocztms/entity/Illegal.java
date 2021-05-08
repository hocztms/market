package com.hocztms.entity;

import com.baomidou.mybatisplus.annotation.Version;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Illegal {
    private String username;
    private int num;
    private int status;

    @Version
    private int version;
}
