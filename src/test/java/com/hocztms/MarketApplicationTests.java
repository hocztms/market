package com.hocztms;



import com.hocztms.mapper.*;
import com.hocztms.service.*;
import com.hocztms.utils.GoodsUtils;
import com.hocztms.webSocket.WebSocketServer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.annotation.Resource;
import java.util.*;


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

    @Autowired
    private UserMessageService userMessageService;

    @Test
    public void test(){
    }
}

