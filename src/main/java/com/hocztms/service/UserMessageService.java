package com.hocztms.service;

import com.hocztms.common.RestResult;
import com.hocztms.entity.Message;
import com.hocztms.vo.MessageVo;

import java.util.List;

public interface UserMessageService{


    RestResult deleteUserMessageByIds(List<Long> ids,String username);

    RestResult updateUserMessageReadTagByIds(List<Long> ids, String username);

    RestResult userFeedback(MessageVo messageVo,String username);

    Integer sendUsersMessage(String username,String msg,long tag,long id);

    Integer sendAdminGoodsMessage();

    Integer sendAdminReportMessage();

    RestResult findUserMsgByUsername(String username);

    List<Message> findMsgByUsername(String username);

    String jsonTOStringWebSocketMsg(int code,Message message);
}
