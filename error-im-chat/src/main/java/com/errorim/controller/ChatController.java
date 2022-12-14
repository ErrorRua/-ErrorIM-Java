package com.errorim.controller;

import com.errorim.entity.Message;
import com.errorim.resolver.PostRequestParam;
import com.errorim.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.web.bind.annotation.*;

/**
 * @author ErrorRua
 * @date 2022/12/11
 * @description:
 */

@RestController
@RequestMapping("/chat")
public class ChatController {
    @Autowired
    private ChatService chatService;

    @MessageMapping("/sendMsg")
    public void sendMsg(Message message) {
        chatService.sendMessage(message);
    }

    @GetMapping("/get-offline-message")
    public void getOfflineMessage() {
        chatService.getOfflineMessage();
    }

    @PostMapping("/ack-offline-message")
    public void ackOfflineMessage(@PostRequestParam String fromUserId) {
        chatService.ackOfflineMessage(fromUserId);
    }


}
