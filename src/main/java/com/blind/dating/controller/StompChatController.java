package com.blind.dating.controller;

import com.blind.dating.domain.Chat;
import com.blind.dating.dto.chat.ChatMessageDto;
import com.blind.dating.dto.chat.ChatRequestDto;
import com.blind.dating.dto.user.UserSession;
import com.blind.dating.handler.SessionHandler;
import com.blind.dating.service.ChatService;
import com.blind.dating.service.ReadChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Controller
@RequiredArgsConstructor
public class StompChatController {

    private final SimpMessagingTemplate template; //특정 Broker로 메세지를 전달
    private final ChatService chatService;
    private final ReadChatService readChatService;


    //Client가 SEND할 수 있는 경로
    //stompConfig에서 설정한 applicationDestinationPrefixes와 @MessageMapping 경로가 병합됨
    //"/pub/chat/enter"
    @MessageMapping(value = "/chat/enter")
    public void enter(ChatRequestDto message, @Header("simpSessionId") String sessionId){
        System.out.println(sessionId+"@@@@@@@@@@@@@@@@@@@@@@@@@@@");
        message.setMessage(message.getWriterId() + "님이 채팅방에 참여하였습니다.");
        template.convertAndSend("/sub/chat/room/" + message.getChatRoomId(), message);
    }

    // roomId, userId, message,
    @MessageMapping(value = "/chat/message")
    public void message(ChatRequestDto message){
        // 일단 채팅방에 메세지 저장하고
        Chat chat = chatService.saveChat(message);
        // 현재 접속중인 사람만 읽은 메세지 update해주기.
        readChatService.updateReadChat(message.getChatRoomId(), chat.getId());
        template.convertAndSend("/sub/chat/room/" + message.getChatRoomId(), message);
    }

    @MessageMapping(value = "/chat/disconnect")
    public void disconnect(){

    }
}
