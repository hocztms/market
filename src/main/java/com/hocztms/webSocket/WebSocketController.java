package com.hocztms.webSocket;

import com.hocztms.entity.Message;
import com.hocztms.service.UserMessageService;
import com.hocztms.springSecurity.jwt.JwtAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Date;

@RestController
@RequestMapping("/socket")
public class WebSocketController {

    @Autowired
    private UserMessageService userMessageService;
    @Autowired
    private JwtAuthService jwtAuthService;


    @PostMapping(value = "/sendMsg")
    public void testSocket1(@RequestBody String msg,HttpServletRequest request) {
        String username = jwtAuthService.getTokenUsername(request);
        System.out.println(username);
        userMessageService.sendUsersMessage(username,"二手交易市场",0,4);
    }

}