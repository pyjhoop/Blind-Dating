package com.blind.dating.dto.chat;

import com.blind.dating.domain.ChatRoom;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ChatRoomDto {

    private Long roomId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String user1Nickname;
    private String user2Nickname;

    public ChatRoomDto(Long roomId, LocalDateTime createdAt, LocalDateTime updatedAt, String user1Nickname, String user2Nickname) {
        this.roomId = roomId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.user1Nickname = user1Nickname;
        this.user2Nickname = user2Nickname;
    }

    public static ChatRoomDto from(ChatRoom room){
        return new ChatRoomDto(room.getId(), room.getCreatedAt(), room.getUpdatedAt(),room.getUser1().getNickname(), room.getUser2().getNickname());
    }
}
