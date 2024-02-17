package com.blind.dating.config;

import com.blind.dating.handler.CustomWebSocketInterceptor;
import com.blind.dating.handler.SocketHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.config.annotation.*;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RequiredArgsConstructor
@Configuration
@EnableWebSocketMessageBroker
public class StompWebSocketConfig implements WebSocketMessageBrokerConfigurer {
    private final SocketHandler socketHandler;
    private final CustomWebSocketInterceptor customWebSocketInterceptor;
    // 세션 관리를 위한 맵을 선언합니다.
    private final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/stomp/chat","/stomp/chatroom")
                .setAllowedOrigins("https://blind-dating-fe.vercel.app","http://localhost:5500","http://127.0.0.1:5500", "http://127.0.0.1:3000","http://localhost:3000", "http://localhost:80","https://fe-zeta.vercel.app")
                .withSockJS();
    }

    /*어플리케이션 내부에서 사용할 path를 지정할 수 있음*/
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.setApplicationDestinationPrefixes("/pub");
        registry.enableSimpleBroker("/sub");
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(customWebSocketInterceptor);
    }

}
