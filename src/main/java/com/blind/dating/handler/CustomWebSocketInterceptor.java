package com.blind.dating.handler;

import com.blind.dating.domain.Chat;
import com.blind.dating.domain.ChatRoom;
import com.blind.dating.domain.ReadChat;
import com.blind.dating.dto.user.UserSession;
import com.blind.dating.handler.SessionHandler;
import com.blind.dating.repository.ChatRepository;
import com.blind.dating.repository.ChattingRoomRepository;
import com.blind.dating.repository.ReadChatRepository;
import com.blind.dating.repository.SessionRedisRepository;
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
    private final SessionRedisRepository sessionRedisRepository;
    private final ChattingRoomRepository chattingRoomRepository;

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


                //방번호로 접속해있는 유저를 찾기 위해 세션에 방번호 저장.
                accessor.getSessionAttributes().put("roomId",roomId);
                accessor.getSessionAttributes().put("userId", userId);

                Optional<Chat> chat = chatRepository.findFirstByChatRoomIdOrderByCreatedAtDesc(Long.valueOf(roomId));

                Long chatId = 0L;
                if(chat.isPresent()){
                    chatId = chat.get().getId();
                }

                ChatRoom chatRoom = chattingRoomRepository.findById(Long.valueOf(roomId))
                        .orElseThrow(()-> new RuntimeException("채팅방 소켓 연결중에 예외가 발생했습니다."));

                ReadChat readChat = readChatRepository.findByChatRoomAndUserId(chatRoom, Long.valueOf(userId))
                        .orElseThrow(()-> new RuntimeException("채팅방 소켓 연결중에 예외가 발생했습니다."));

                readChat.setChatId(chatId);
                readChatRepository.save(readChat);
                sessionRedisRepository.saveUserId(roomId, userId);

                break;
            case DISCONNECT:

                // 세션 종료 처리
                String roomId1 = accessor.getSessionAttributes().get("roomId").toString();
                String userId1 = accessor.getSessionAttributes().get("userId").toString();

                sessionRedisRepository.removeUserId(roomId1, userId1);

                break;
            default:
                break;
        }

        return message;
    }
}

