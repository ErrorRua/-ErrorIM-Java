package com.errorim.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author ErrorRua
 * @date 2022/12/08
 * @description:
 */
@Configuration
@EnableWebSocketMessageBroker
public class StompWebSocketConfig implements WebSocketMessageBrokerConfigurer {

    // TODO 暂时使用内存存储，后期改为redis
    @Bean
    public ConcurrentHashMap<String, String> userMap() {
        return new ConcurrentHashMap<>(48);
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // 开启一个简单的基于内存的消息代理
        // 将消息返回到订阅了带 /chat 前缀的目的客户端
        config.enableSimpleBroker("/websocket/chat");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {

        registry.addEndpoint("/websocket").setAllowedOriginPatterns("*")
                .withSockJS();


    }

}