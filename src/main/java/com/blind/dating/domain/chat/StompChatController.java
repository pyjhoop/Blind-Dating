package com.blind.dating.domain.chat;

import com.blind.dating.dto.chat.*;
import com.blind.dating.domain.redis.SessionRedisRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

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
