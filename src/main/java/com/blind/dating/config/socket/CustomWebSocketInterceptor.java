package com.blind.dating.config.socket;

import com.blind.dating.domain.chat.ChatRepository;
import com.blind.dating.domain.chatRoom.ChattingRoomRepository;
import com.blind.dating.domain.readChat.ReadChatRepository;
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
    private final SessionManager sessionManager;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

//        System.out.println(">>>"+accessor.getDestination());
//        String destination = accessor.getDestination();
//        if (destination.contains("chat-room")) {
//            return message;
//        } else if(destination == null) {
//
//        }



        // CONNECT, DISCONNECT 등 상태를 알아내줌
        StompCommand command = accessor.getCommand();

        if (command == null) {
            return message;
        }

        switch (command) {
            case CONNECT:
                // 세션 연결 처리
                try{
                    String roomId = accessor.getNativeHeader("roomId").get(0);
                    String userId = accessor.getNativeHeader("userId").get(0);

                    //웹소켓 세션 해제시 session매니저에 있는 정보도 해제하기 위함.
                    accessor.getSessionAttributes().put("roomId", roomId);
                    accessor.getSessionAttributes().put("userId", userId);

                    // 세션에 채팅방 입장 유저 정보 저장
                    sessionManager.addSession(userId, roomId);
                    break;
                }catch (NullPointerException e){
                    return message;
                }

            case DISCONNECT:

                String userId1 = (String) accessor.getSessionAttributes().get("userId");
                if(userId1 == null) return message;
                String roomId1 = (String) accessor.getSessionAttributes().get("roomId");

                // 세션 해제
                sessionManager.removeSession(userId1, roomId1);

                break;
            default:
                break;
        }

        return message;
    }
}

