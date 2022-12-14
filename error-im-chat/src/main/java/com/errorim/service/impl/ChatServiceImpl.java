package com.errorim.service.impl;

import com.errorim.asyn.AsyncChatTask;
import com.errorim.entity.Message;
import com.errorim.entity.ResponseResult;
import com.errorim.service.ChatService;
import com.errorim.util.BeanCopyUtils;
import com.errorim.util.RedisCache;
import com.errorim.util.SecurityUtils;
import com.errorim.vo.MessageVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.*;

import static com.errorim.constant.ChatConstant.CACHE_MESSAGE_KEY;

/**
 * @author ErrorRua
 * @date 2022/12/11
 * @description:
 */
@Service
public class ChatServiceImpl implements ChatService {

    @Autowired
    private AsyncChatTask asyncChatTask;

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @Autowired
    private RedisCache redisCache;

    @Autowired
    private ConcurrentMap<String, String> userMap;

    @Override
    public void sendMessage(Message message) {
        Future<Message> messageFuture = asyncChatTask.saveMessage(message);
        if (!userMap.containsValue(message.getToUserId())) {
            String key = CACHE_MESSAGE_KEY + message.getToUserId() + ":" + message.getFromUserId();
            try {
                redisCache.addMessage(key, BeanCopyUtils.beanToMap(messageFuture.get(5, TimeUnit.MINUTES)));
            } catch (InterruptedException | ExecutionException | TimeoutException e) {
                throw new RuntimeException(e);
            }
        }

        MessageVO messageVO = BeanCopyUtils.copyBean(message, MessageVO.class);
        messageVO.setSendTime(new Date());

        simpMessagingTemplate
                .convertAndSend("/websocket/chat/contact/" + message.getToUserId()
                        , messageVO);
    }

    @Override
    public ResponseResult getOfflineMessage() {
        String userId = SecurityUtils.getUserId();
        String key = CACHE_MESSAGE_KEY + userId;

        System.out.println("key = " + key);
        redisCache.keys(key + "*").forEach(k -> {
            redisCache.readMessage(k).forEach(m -> {
                MessageVO message = BeanCopyUtils.mapToBean((Map) m.getValue(), MessageVO.class);
                simpMessagingTemplate.convertAndSend("/websocket/chat/contact/" + userId, message);
            });
        });
        return null;
    }

    @Override
    public ResponseResult ackOfflineMessage(String fromUserId) {
        String userId = SecurityUtils.getUserId();

        String key = CACHE_MESSAGE_KEY + userId + ":" + fromUserId;

        redisCache.deleteObject(key);

        return ResponseResult.okResult();
    }
}
