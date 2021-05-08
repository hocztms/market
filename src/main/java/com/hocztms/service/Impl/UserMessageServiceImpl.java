package com.hocztms.service.Impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hocztms.common.RestResult;
import com.hocztms.entity.Message;
import com.hocztms.mapper.MessageMapper;
import com.hocztms.service.UserMessageService;
import com.hocztms.vo.MessageVo;
import com.hocztms.vo.SocketMessage;
import com.hocztms.webSocket.WebSocketServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.awt.geom.QuadCurve2D;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class UserMessageServiceImpl implements UserMessageService {

    @Autowired
    private MessageMapper messageMapper;

    @Autowired
    private WebSocketServer webSocketServer;

    @Override
    public RestResult deleteUserMessageByIds(List<Long> ids, String username) {
        try {
            RestResult result = new RestResult(1,"操作成功",null);
            for (Long id:ids){
                if (!messageMapper.selectById(id).getUsername().equals(username)){
                    result.put("error",id+"删除失败,无权限");
                    log.warn(username + "正在进行违法操作......");
                }
                else {
                    messageMapper.deleteById(id);
                }
            }
            return result;
        }catch (Exception e){
            return new RestResult(0,"操作失败",null);
        }
    }

    @Override
    public RestResult updateUserMessageReadTagByIds(List<Long> ids, String username) {
        try {
            RestResult result = new RestResult(1,"操作成功",null);
            for (Long id:ids){
                Message message = messageMapper.selectById(id);
                if (!message.getUsername().equals(username)){
                    result.put("error",id+"删除失败,无权限");
                    log.warn(username + "正在进行违法操作......");
                }
                else {
                    message.setReadTag(1);
                    messageMapper.updateById(message);
                }
            }
            return result;
        }catch (Exception e){
            return new RestResult(0,"操作失败",null);
        }
    }

    @Override
    public RestResult userFeedback(MessageVo messageVo, String username) {
        try {
            Message message=new Message(0,messageVo.getObjectTag(),messageVo.getObjectId(),"admin",messageVo.getMsg(),username,new Date(),0);
            messageMapper.insert(message);

            webSocketServer.sendInfo(message.getUsername(),jsonTOStringWebSocketMsg(1,message));
            return new RestResult(1,"操作成功",null);

        }catch (Exception e){
            return new RestResult(0,"操作失败",null);
        }
    }

    @Override
    public Integer sendUsersMessage(String username,String msg,long tag,long id) {
        Message message=new Message(0,tag,id,username,msg,"二手交易市场",new Date(),0);
        try {
            messageMapper.insert(message);
        }catch (Exception e){
            return 0;
        }

        webSocketServer.sendInfo(message.getUsername(),jsonTOStringWebSocketMsg(1,message));
        return 1;
    }

    @Override
    public Integer sendAdminGoodsMessage() {
        try {
            SocketMessage message = new SocketMessage(0, "你有新的商品待审核请刷新");
            webSocketServer.sendInfo("admin", JSON.toJSONString(message));
            return 1;
        }catch (Exception e){
            return 0;
        }
    }

    @Override
    public Integer sendAdminReportMessage() {
        try {
            SocketMessage message = new SocketMessage(0, "你有新的举报消息待审核请刷新");
            webSocketServer.sendInfo("admin", JSON.toJSONString(message));
            return 1;
        }catch (Exception e){
            return 0;
        }
    }

    @Override
    public RestResult findUserMsgByUsername(String username) {
        try {
            List<Message> messages = findMsgByUsername(username);
            return new RestResult(1,"操作成功",messages);
        }catch (Exception e) {
            return new RestResult(0, "失败", null);
        }
    }

    @Override
    public List<Message> findMsgByUsername(String username) {
        QueryWrapper<Message> wrapper = new QueryWrapper<>();
        wrapper.eq("username",username);
        wrapper.orderByAsc("read_tag");
        wrapper.orderByDesc("date");
        return messageMapper.selectList(wrapper);
    }

    @Override
    public String jsonTOStringWebSocketMsg(int code,Message message){
        SocketMessage socketMessage = new SocketMessage(code,message);
        return JSON.toJSONString(socketMessage);
    }
}
