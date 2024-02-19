package com.blind.dating.config.socket;

import com.blind.dating.config.redis.Publisher;
import com.blind.dating.config.redis.Subscriber;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
@Component
public class SocketTextHandler extends TextWebSocketHandler {


    private final WebSocketSessionManager webSocketSessionManager;

    private final Publisher redisPublisher;

    private final Subscriber redisSubscriber;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        webSocketSessionManager.addWebSocketSession(session);
        // 여기서 rooms or room으로 나뉘어야 해. 룸 아이디를 channel로 해도 되는거지.
        String roomId = WebSocketHelper.getRoomIdFromSessionAttribute(session);
        String userId = WebSocketHelper.getUserIdFromSessionAttribute(session);
        String chanel = "room/"+roomId+"/user/"+userId;
        System.out.println("subscribe Chanel"+ chanel);
        redisSubscriber.subscribe(chanel);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        webSocketSessionManager.removeWebSocketSession(session);
        String userId = WebSocketHelper.getUserIdFromSessionAttribute(session);
        String roomId = WebSocketHelper.getRoomIdFromSessionAttribute(session);
        String chanel = "room/"+roomId+"/user/"+ userId;
        redisSubscriber.unsubscribe(chanel);
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message)
            throws IOException {
        String payload = message.getPayload();
        String[] payLoadSplit = payload.split("->");
        String targetRoomId  = payLoadSplit[0].trim();
        String messageToBeSent = payLoadSplit[1].trim();
        String userId = WebSocketHelper.getUserIdFromSessionAttribute(session);
        log.info("got the payload {} and going to send to channel {}", payload, targetRoomId);
        this.redisPublisher.publish(targetRoomId, userId + ":" + messageToBeSent);
    }
}