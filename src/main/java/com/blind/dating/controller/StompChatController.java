package com.blind.dating.controller;

import com.blind.dating.domain.Chat;
import com.blind.dating.domain.ChatRoom;
import com.blind.dating.domain.UserAccount;
import com.blind.dating.dto.chat.ChatRequestDto;
import com.blind.dating.dto.chat.ChatRoomDto;
import com.blind.dating.dto.response.ResponseDto;
import com.blind.dating.handler.SessionHandler;
import com.blind.dating.service.ChatService;
import com.blind.dating.service.ChattingRoomService;
import com.blind.dating.service.ReadChatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;

import java.util.List;

@Tag(name = "Stomp Info", description = "STOMP 관련 서비스")
@Controller
@RequiredArgsConstructor
public class StompChatController {

    private final SimpMessagingTemplate template; //특정 Broker로 메세지를 전달
    private final ChatService chatService;
    private final ReadChatService readChatService;
    private final ChattingRoomService chattingRoomService;
    private final SessionHandler sessionHandler;


    //Client가 SEND할 수 있는 경로
    //stompConfig에서 설정한 applicationDestinationPrefixes와 @MessageMapping 경로가 병합됨
    //"/pub/chat/enter"
//    @MessageMapping(value = "/chat/enter")
//    public void enter(ChatRequestDto message, @Header("simpSessionId") String sessionId){
//        System.out.println(sessionId+"@@@@@@@@@@@@@@@@@@@@@@@@@@@");
//        message.setMessage(message.getWriterId() + "님이 채팅방에 참여하였습니다.");
//        template.convertAndSend("/sub/chat/room/" + message.getChatRoomId(), message);
//    }

    // roomId, userId, message,
    @MessageMapping(value = "/chat/message")
    @Operation(summary = "메세지를 전송", description = "채팅방에 접속중인 유저에게 메세지를 전송합니다.")
    public void message(ChatRequestDto message){
        // 일단 채팅방에 메세지 저장하고
        Chat chat = chatService.saveChat(message);
        // 현재 접속중인 사람만 읽은 메세지 update해주기.
        readChatService.updateReadChat(message.getChatRoomId(), chat.getId());
        // 내 채팅방 가져와서 현제 writer외의 다른 사람의 rooms를 가져와서 convert해주기
        ChatRoom room = chattingRoomService.getRoom(message.getChatRoomId()).get();
        Long userId = 0L;
        if(room.getUser1() == Long.valueOf(message.getWriterId())){
            userId = room.getUser2();
        }else{
            userId = room.getUser1();
        }

        List<ChatRoomDto> rooms = chattingRoomService.getRooms(userId);
        template.convertAndSend("/sub/chatroom/"+userId, rooms);
        template.convertAndSend("/sub/chat/room/" + message.getChatRoomId(), message);
    }

    @MessageMapping(value = "/chat/disconnect")
    @Operation(summary = "채팅방 나가기", description = "채팅방 나갈시 다른 유저에게 메세지를 전송합니다.")
    public void leave(ChatRequestDto message){
        //채팅방 떠나는 기능
        ChatRoom room = chattingRoomService.leaveChatRoom(message.getChatRoomId(), message.getWriterId());
        //채팅방에 아무도 없을 때 채팅방 삭제
        boolean check = chattingRoomService.removeRoom(room);
        String response = "";

        if(!check){
            message.setMessage("상대방이 채팅방을 나가셨습니다.");
            template.convertAndSend("/sub/chat/room/"+message.getChatRoomId(), message);
        }


    }
}
