package com.errorim.listener;//这边使用Hutool的JSON工具类进行JSON解析，详细使用方式详见 hutool.cn

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;
import org.springframework.web.socket.messaging.SessionUnsubscribeEvent;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * WebSocket客户端状态监听
 *
 * @author Tony Peng
 * @date 2022/10/27 11:19
 */
@Slf4j
@Component
public class WebSocketEventListener {

    @Autowired
    private Set<String> userSet;

    /**
     * 监听客户端连接
     *
     * @param event 连接事件对象
     */
    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {

        // 依托答辩

        StompHeaderAccessor wrap = StompHeaderAccessor.wrap(event.getMessage());
        Object o = ((Map<String, List>) ((GenericMessage<?>) wrap.getHeader("simpConnectMessage"))
                .getHeaders()
                .get("nativeHeaders"))
                .get("id").get(0);

        userSet.add(o.toString());

        log.info("用户连接:{}", o.toString());
        log.info("当前在线用户数:{}", userSet.size());
        log.info("当前在线用户:{}", userSet);

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

    }

    /**
     * 监听客户端取消订阅事件
     *
     * @param event 取消订阅事件对象
     */
    @EventListener
    public void handleUnSubscription(SessionUnsubscribeEvent event) {

    }
}
