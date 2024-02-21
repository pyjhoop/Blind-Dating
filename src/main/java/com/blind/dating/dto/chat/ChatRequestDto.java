package com.blind.dating.dto.chat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatRequestDto {
    private String chatRoomId;
    private String writerId;
    private String message;
    private String receiverId;

}
