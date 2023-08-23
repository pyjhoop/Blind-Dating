package com.blind.dating.dto.chat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

@Data
@NoArgsConstructor
public class ChatListWithOtherUserInfo {
    private Long otherUserId;
    private String otherUserNickname;
    private Page<ChatDto> chatList;

    private ChatListWithOtherUserInfo(Long otherUserId, String otherUserNickname, Page<ChatDto> chatList) {
        this.otherUserId = otherUserId;
        this.otherUserNickname = otherUserNickname;
        this.chatList = chatList;
    }

    public static ChatListWithOtherUserInfo of(Long otherUserId, String otherUserNickname, Page<ChatDto> chatList){
        return new ChatListWithOtherUserInfo(otherUserId, otherUserNickname, chatList);
    }
}
