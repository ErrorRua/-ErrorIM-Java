package com.errorim.asyn;

import com.errorim.entity.Message;
import com.errorim.mapper.MessageMapper;
import com.errorim.util.BeanCopyUtils;
import com.errorim.util.RedisCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Future;

import static com.errorim.constant.ChatConstant.CACHE_MESSAGE_KEY;

/**
 * @author ErrorRua
 * @date 2022/12/11
 * @description:
 */
@Async("taskExecutor")
@Component
public class AsyncChatTask {
    @Autowired
    private MessageMapper messageMapper;

    @Autowired
    private RedisCache redisCache;

    @Autowired
    private ConcurrentMap<String, String> userMap;

    public Future<Message> saveMessage(Message message) {
        message.setSendTime(new Date());

        messageMapper.insert(message);
        return new AsyncResult<>(message);
    }

    public void saveOfflineMessage(Message message) {
        String key = CACHE_MESSAGE_KEY + message.getToUserId() + ":" + message.getFromUserId();
        redisCache.addMessage(key, BeanCopyUtils.beanToMap(message));
    }
}
