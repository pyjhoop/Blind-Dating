package com.blind.dating.handler;

import com.blind.dating.dto.user.UserSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;

import java.security.Principal;

/**
 * 웹 소켓에 연결되거나 연결이 끊겼을때 session을 관리하기 위한 인터셉터
 */
@RequiredArgsConstructor
@Slf4j
@Component
public class CustomWebSocketInterceptor implements ChannelInterceptor {

    // session을 관리하는 핸들러
    private final SessionHandler sessionHandler;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

        // CONNECT, DISCONNECT 등 상태를 알아내줌
        StompCommand command = accessor.getCommand();

        System.out.println(command);
        System.out.println("+======================");

        if (command == null) {
            return message;
        }

        switch (command) {
            case CONNECT:
                // 세션 연결 처리
                String username = accessor.getNativeHeader("username").get(0);
                String roomId = accessor.getNativeHeader("roomId").get(0);
                String userId = accessor.getNativeHeader("userId").get(0);

                //방번호로 접속해있는 유저를 찾기 위해 세션에 방번호 저장.
                accessor.getSessionAttributes().put("roomId",roomId);
                System.out.println(username);

                // 접속중인 유저 정보 인스턴스 생성
                UserSession userSession = new UserSession(username, userId, accessor.getSessionId(), roomId);

                System.out.println(accessor.getSessionId());
                //유저정보 sessionhandler에 저장하기.
                sessionHandler.addSession(userSession);
                System.out.println(sessionHandler.getUsers(roomId));
                break;
            case DISCONNECT:
                // 세션 종료 처리
//                Principal principal = accessor.getUser();
//                if (principal != null) {
//                    String sessionId = principal.getName();
//                    sessionHandler.removeSession(sessionId);
//                }
                System.out.println("방번호 ");
                System.out.println(accessor.getSessionAttributes().get("roomId"));

                String roomId1 = accessor.getSessionAttributes().get("roomId").toString();
                String sessionId = accessor.getSessionId();
                sessionHandler.removeSession(roomId1,sessionId);

                System.out.println(sessionHandler.getUsers(roomId1));
                break;
            default:
                break;
        }

        return message;
    }
}

