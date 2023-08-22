package com.blind.dating.handler;

import com.blind.dating.dto.user.UserSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.simp.stomp.StompSessionHandler;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.springframework.web.socket.messaging.SessionConnectEvent;

//@Slf4j
//@Service
//public class WebSocketHandler extends TextWebSocketHandler {
//    private StompSessionHandler stompSessionHandler;
//    private SessionHandler sessionHandler;
//
//
//    @Override
//    public void afterConnectionEstablished(WebSocketSession session) {
//        String userName = session.getHandshakeHeaders().getFirst("username");
//        String userId = session.getHandshakeHeaders().getFirst("userId");
//        String roomId = session.getHandshakeHeaders().getFirst("roomId");
//
//        UserSession userSession = new UserSession(userId,userName, roomId);
//        log.warn(userSession.toString());
//
//        sessionHandler.addSession(session, userSession);
//    }
//
//    @Override
//    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
//        log.info(session.toString()+"제거");
//        sessionHandler.removeSession(session);
//    }
//}