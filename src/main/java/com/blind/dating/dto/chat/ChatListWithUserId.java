package com.blind.dating.dto.chat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatListWithUserId {

    private Long userId;
    private List<ChatRoomDto> rooms;
}
