package com.hocztms;



import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hocztms.common.RestResult;
import com.hocztms.entity.*;
import com.hocztms.mapper.*;
import com.hocztms.service.*;
import com.hocztms.springSecurity.jwt.JwtTokenUtils;
import com.hocztms.utils.GoodsUtils;
import com.hocztms.utils.RedisUtils;
import com.hocztms.utils.WebUtils;
import com.hocztms.vo.ReportVo;
import com.hocztms.webSocket.WebSocketServer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class MarketApplicationTests {

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    UserService userService;

    @Autowired
    RoleMapper roleMapper;

    @Autowired
    GoodsMapper goodsMapper;

    @Autowired
    GoodsService goodsService;

    @Autowired
    GoodsUtils goodsUtils;

    @Autowired
    PictureMapper pictureMapper;

    @Autowired
    AddressMapper addressMapper;

    @Autowired
    UsersMapper usersMapper;

    @Autowired
    AddressService addressService;

    @Autowired
    WebUtils webUtils;

    @Autowired
    OrderFormMapper orderFormMapper;

    @Autowired
    OrderFormService orderFormService;

    @Autowired
    LabelMapper labelMapper;

    @Autowired
    LabelService labelService;

    @Autowired
    MessageMapper messageMapper;

    @Autowired
    UserCollectionService userCollectionService;

    @Autowired
    ReportService reportService;

    @Autowired
    IllegalMapper illegalMapper;

    @Autowired
    private RedisTemplate<String,Date> jwtRedisTemplate;

    @Resource
    private RedisTemplate<String,String> stringRedisTemplate;

    @Autowired
    private WebSocketServer webSocketServer;
}

