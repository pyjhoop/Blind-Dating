package com.blind.dating.domain.chat;

import com.blind.dating.config.socket.SessionManager;
import com.blind.dating.domain.chatRoom.ChatRoom;
import com.blind.dating.domain.chatRoom.ChattingRoomService;
import com.blind.dating.dto.chat.*;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Controller
@RequiredArgsConstructor
public class StompChatController {

    private final ChatService chatService;
    private final SimpMessageSendingOperations messageTemplate;
    private final SessionManager sessionManager;
    private final ChattingRoomService chattingRoomService;

    @MessageMapping(value = "/chat/{roomId}")
    public void message(@DestinationVariable Long roomId, ChatRequestDto dto){
        Chat chat = chatService.saveChat(dto);
        messageTemplate.convertAndSend("/sub/chat/"+roomId, ChatDto.from(chat));
        // 스레드 생성해서 진행하면 되듯
        // 세션 두명있는지 확인
        //TODO 읽지 않은 메시지 개수도 리턴해주고 아니야 한명이 있을땐 실시간성이 좀 떨어져도 됨.
        ConcurrentHashMap<String, String> users = sessionManager.getUsers(String.valueOf(roomId));
        if(users.size() <2) {
            List<ChatRoomDto> chatRooms = chattingRoomService.getRooms(Long.valueOf(dto.getReceiverId()));

            messageTemplate.convertAndSend("/sub/chat-room/"+dto.getReceiverId(), chatRooms);
        }

    }

}
