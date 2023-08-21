package com.blind.dating.dto.chat;

import lombok.Data;
import org.springframework.web.socket.WebSocketSession;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Data
public class ChatRoomDtos {
    private String roomId;
    private String name;
    private Set<WebSocketSession> sessions = new HashSet<>();

    //WebSocketSession은 Spring에서 Websocket Connection이 맺어진 세션

    public static ChatRoomDtos create(String name){
        ChatRoomDtos room = new ChatRoomDtos();

        room.roomId = UUID.randomUUID().toString();
        room.name = name;
        return room;
    }
}
