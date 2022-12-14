package com.errorim.listener;//这边使用Hutool的JSON工具类进行JSON解析，详细使用方式详见 hutool.cn

import com.errorim.entity.Message;
import com.errorim.util.BeanCopyUtils;
import com.errorim.util.RedisCache;
import com.errorim.vo.MessageVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;
import org.springframework.web.socket.messaging.SessionUnsubscribeEvent;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.errorim.constant.ChatConstant.CACHE_MESSAGE_KEY;

/**
 * WebSocket客户端状态监听
 *
 * @author Tony Peng
 * @date 2022/10/27 11:19
 */
@Slf4j
@Component
public class ChatWebSocketEventListener {
    private final ConcurrentHashMap<String, String> userMap;

    private final RedisCache redisCache;

    private final SimpMessagingTemplate simpMessagingTemplate;

    public ChatWebSocketEventListener(ConcurrentHashMap<String, String> userMap, RedisCache redisCache, SimpMessagingTemplate simpMessagingTemplate) {
        this.userMap = userMap;
        this.redisCache = redisCache;
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    /**
     * 监听客户端连接
     *
     * @param event 连接事件对象
     */
    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {

    }

    /**
     * 监听客户端关闭事件
     *
     * @param event 关闭事件对象
     */
    @EventListener
    public void handleWebSocketCloseListener(SessionDisconnectEvent event) {

    }

    /**
     * 监听客户端订阅事件
     *
     * @param event 订阅事件对象
     */
    @EventListener
    public void handleSubscription(SessionSubscribeEvent event) {
        StompHeaderAccessor wrap = StompHeaderAccessor.wrap(event.getMessage());
        String userId = userMap.get(wrap.getSessionId());

        String key = CACHE_MESSAGE_KEY + userId;

        System.out.println("key = " + key);
        redisCache.keys(key + "*").forEach(k -> {
            redisCache.readMessage(k).forEach(m -> {
                MessageVO message = BeanCopyUtils.mapToBean((Map) m.getValue(), MessageVO.class);
                simpMessagingTemplate.convertAndSend("/websocket/chat/contact/" + userId, message);
            });

            // 删除已经发送的离线消息
            // TODO: 这里离线消息只能发送一次，后续需要改进
            redisCache.deleteObject(k);
        });
    }

    /**
     * 监听客户端取消订阅事件
     *
     * @param event 取消订阅事件对象
     */
    @EventListener
    public void handleUnSubscription(SessionUnsubscribeEvent event) {

    }

    private String getUserId(StompHeaderAccessor wrap) {
        return (String) ((Map<String, List>) ((GenericMessage<?>) wrap.getHeader("simpConnectMessage"))
                .getHeaders()
                .get("nativeHeaders"))
                .get("userId").get(0);
    }
}
