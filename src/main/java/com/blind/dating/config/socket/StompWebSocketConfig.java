package com.blind.dating.config.socket;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.*;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

@RequiredArgsConstructor
@Configuration
@EnableWebSocket
public class StompWebSocketConfig implements WebSocketConfigurer {
    private final SocketTextHandler socketTextHandler;

//    @Override
//    public void registerStompEndpoints(StompEndpointRegistry registry) {
//        registry.addEndpoint("/stomp/chat","/stomp/chatroom")
//                .setAllowedOrigins("https://blind-dating-fe.vercel.app","http://localhost:5500","http://127.0.0.1:5500", "http://127.0.0.1:3000","http://localhost:3000", "http://localhost:80","https://fe-zeta.vercel.app");
//    }

    // 채팅방 리스트 : rooms/userId, 채팅방 : /room/roomId/user/userId
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(socketTextHandler, "/rooms/*", "/room/**").
                addInterceptors(getParametersInterceptors()).
                setAllowedOriginPatterns("*");
    }

    @Bean
    public HandshakeInterceptor getParametersInterceptors() {
        return new HandshakeInterceptor() {
            @Override
            public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
                String path = request.getURI().getPath();

                String userId = WebSocketHelper.getUserIdFromUrl(path);
                if(path.contains("rooms")){
                    attributes.put(WebSocketHelper.userIdKey, userId);

                } else {
                    String roomId = WebSocketHelper.getRoomIdFromUrl(path);
                    attributes.put(WebSocketHelper.userIdKey, userId);
                    attributes.put(WebSocketHelper.roomIdKey, roomId);
                }
                return true;
            }

            public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                       WebSocketHandler wsHandler, Exception exception) {
                // Nothing to do after handshake
            }
        };
    }



}
