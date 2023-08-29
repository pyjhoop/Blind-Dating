package com.blind.dating.dto.chat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.List;

@Data
@NoArgsConstructor
public class ChatListWithOtherUserInfo {
    private Long otherUserId;
    private String otherUserNickname;
    private Boolean roomStatus;
    private List<ChatDto> chatList;

    private ChatListWithOtherUserInfo(Long otherUserId, String otherUserNickname, Boolean roomStatus,List<ChatDto> chatList) {
        this.otherUserId = otherUserId;
        this.otherUserNickname = otherUserNickname;
        this.roomStatus = roomStatus;
        this.chatList = chatList;
    }

    public static ChatListWithOtherUserInfo of(Long otherUserId, String otherUserNickname, Boolean roomStatus, List<ChatDto> chatList){
        return new ChatListWithOtherUserInfo(otherUserId, otherUserNickname, roomStatus, chatList);
    }
}
