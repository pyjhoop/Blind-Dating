package com.blind.dating.controller;

import com.blind.dating.domain.Chat;
import com.blind.dating.domain.ChatRoom;
import com.blind.dating.domain.UserAccount;
import com.blind.dating.dto.chat.*;
import com.blind.dating.handler.SessionHandler;
import com.blind.dating.repository.SessionRedisRepository;
import com.blind.dating.service.ChatService;
import com.blind.dating.service.ChattingRoomService;
import com.blind.dating.service.ReadChatService;
import com.blind.dating.service.RedisPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class StompChatController {

    private final ChatService chatService;
    private final SessionRedisRepository sessionRedisRepository;


    @MessageMapping(value = "/chat/{roomId}/message")
    @SendTo("/sub/chat/{roomId}/message")
    public ChatDto message(@DestinationVariable Long roomId, ChatRequestDto dto){
        Chat chat = chatService.saveChat(dto);
        return ChatDto.from(chat);
    }
    @MessageMapping(value = "/chat/{roomId}/disconnect")
    public void disconnect(ChatRequestDto dto){
        System.out.println("동작");
        sessionRedisRepository.removeUserId(dto.getChatRoomId(), dto.getWriterId());
    }

}
