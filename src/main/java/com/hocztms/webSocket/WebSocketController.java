package com.hocztms.webSocket;

import com.hocztms.service.UserMessageService;
import com.hocztms.springSecurity.jwt.JwtAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/socket")
public class WebSocketController {

    @Autowired
    private UserMessageService userMessageService;
    @Autowired
    private JwtAuthService jwtAuthService;
    @Autowired
    private WebSocketServer webSocketServer;


    @PostMapping(value = "/sendMsg")
    public void testSocket1(@RequestBody String msg,HttpServletRequest request) {
        String username = jwtAuthService.getTokenUsername(request);
        System.out.println(username);
        userMessageService.sendUsersMessage(username,"二手交易市场",0,4);
    }

    @PostMapping(value = "/test")
    public void testSocket1(HttpServletRequest request) {
        webSocketServer.onClose(request.getHeader("token"));
    }

}