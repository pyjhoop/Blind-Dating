package com.blind.dating.dto.chat;

import lombok.Data;

@Data
public class ChatMessageDto {
    private String roomId;
    private String writerId;
    private String message;
}
