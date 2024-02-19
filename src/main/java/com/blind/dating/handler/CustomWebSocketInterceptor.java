package com.blind.dating.handler;

import com.blind.dating.domain.chat.ChatRepository;
import com.blind.dating.domain.chatRoom.ChattingRoomRepository;
import com.blind.dating.domain.readChat.ReadChatRepository;
import com.blind.dating.config.socket.WebSocketSessionManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 웹 소켓에 연결되거나 연결이 끊겼을때 session을 관리하기 위한 인터셉터
 */
@RequiredArgsConstructor
@Slf4j
@Component
public class CustomWebSocketInterceptor implements ChannelInterceptor {

    // session을 관리하는 핸들러
    private final SessionHandler sessionHandler;
    private final ChatRepository chatRepository;
    private final ReadChatRepository readChatRepository;
    private final WebSocketSessionManager websocketSessionManager;
    private final ChattingRoomRepository chattingRoomRepository;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        log.info(">>>> 하이");
        log.info(accessor.getCommand()+"");

        // CONNECT, DISCONNECT 등 상태를 알아내줌
        StompCommand command = accessor.getCommand();

        if (command == null) {
            return message;
        }

        switch (command) {
            case CONNECT:
                // 세션 연결 처리
                String roomId = accessor.getNativeHeader("roomId").get(0);
                String userId = accessor.getNativeHeader("userId").get(0);

                Map<String, Object> map = new HashMap<>();
                map.put("roomId", roomId);
                map.put("userId",userId);
                accessor.setSessionAttributes(map);

//                sessionRedisRepository.saveUserId(roomId, userId);

                break;
            case DISCONNECT:
                log.info("종료");
                String userId1 = (String) message.getHeaders().get("userId");
                String roomId1 = (String) message.getHeaders().get("roomId");
                System.out.println("User ID: " + userId1 + ", Room ID: " + roomId1);

//                sessionRedisRepository.removeUserId(roomId1, userId1);

                break;
            default:
                break;
        }

        return message;
    }
}

