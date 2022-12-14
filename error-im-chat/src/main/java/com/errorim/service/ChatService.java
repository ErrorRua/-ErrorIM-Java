package com.errorim.service;

import com.errorim.entity.Message;
import com.errorim.entity.ResponseResult;

/**
 * @author ErrorRua
 * @date 2022/12/11
 * @description:
 */
public interface ChatService {

    void sendMessage(Message message);

    ResponseResult getOfflineMessage();

    ResponseResult ackOfflineMessage(String fromUserId);
}
