package com.blind.dating.dto.chat;

import com.blind.dating.domain.ChatRoom;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class ChatRoomDto {

    private Long roomId;
    private LocalDateTime updatedAt;
    private Long otherUserid;
    private String otherUserNickname;
    private String recentMessage;
    private Long unReadCount;

    public ChatRoomDto(Long roomId, LocalDateTime updatedAt, Long otherUserid, String otherUserNickname, String recentMessage) {
        this.roomId = roomId;
        this.updatedAt = updatedAt;
        this.otherUserid = otherUserid;
        this.otherUserNickname = otherUserNickname;
        this.recentMessage = recentMessage;
    }
}
