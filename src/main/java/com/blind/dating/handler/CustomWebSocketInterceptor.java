package com.blind.dating.handler;

import com.blind.dating.domain.Chat;
import com.blind.dating.domain.ReadChat;
import com.blind.dating.dto.user.UserSession;
import com.blind.dating.handler.SessionHandler;
import com.blind.dating.repository.ChatRepository;
import com.blind.dating.repository.ReadChatRepository;
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
import java.util.Optional;

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

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

        // CONNECT, DISCONNECT 등 상태를 알아내줌
        StompCommand command = accessor.getCommand();

        if (command == null) {
            return message;
        }

        switch (command) {
            case CONNECT:
                // 세션 연결 처리
                if(accessor.getNativeHeader("username") == null){
                    break;
                }

                String username = accessor.getNativeHeader("username").get(0);
                String roomId = accessor.getNativeHeader("roomId").get(0);
                String userId = accessor.getNativeHeader("userId").get(0);
                System.out.println("============");
                System.out.println("userName: "+username);
                System.out.println("roomId: "+roomId);
                System.out.println("userId: "+userId);

                //방번호로 접속해있는 유저를 찾기 위해 세션에 방번호 저장.
                accessor.getSessionAttributes().put("roomId",roomId);

                Optional<Chat> chat = chatRepository.findFirstByChatRoomIdOrderByCreatedAtDesc(Long.valueOf(roomId));
                Long chatId = 0L;
                if(chat.isPresent()){
                    chatId = chat.get().getId();
                }

                Optional<ReadChat> readChat = readChatRepository.findByRoomIdAndUserId(Long.valueOf(roomId), Long.valueOf(userId));
                if(readChat.isPresent()){
                    readChat.get().setChatId(chatId);
                    readChatRepository.save(readChat.get());
                }else{
                    readChat = null;
                }

                System.out.println("과연");

                // 접속중인 유저 정보 인스턴스 생성
                UserSession userSession = UserSession.of(username, userId, accessor.getSessionId(), roomId);
                //유저정보 sessionhandler에 저장하기.
                sessionHandler.addSession(userSession);
                break;
            case DISCONNECT:

                // 세션 종료 처리
                String roomId1 = accessor.getSessionAttributes().get("roomId").toString();
                String sessionId = accessor.getSessionId();
                sessionHandler.removeSession(roomId1,sessionId);
                break;
            default:
                break;
        }

        return message;
    }
}

