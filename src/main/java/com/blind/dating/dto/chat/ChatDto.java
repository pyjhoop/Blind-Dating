package com.blind.dating.dto.chat;

import com.blind.dating.domain.Chat;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class ChatDto {

    private Long id;
    private Long writerId;
    private String message;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public ChatDto(Long id, Long writerId, String message, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.writerId = writerId;
        this.message = message;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static ChatDto from(Chat chat){
        return new ChatDto(
                chat.getId(),
                chat.getWriterId(),
                chat.getMessage(),
                chat.getCreatedAt(),
                chat.getUpdatedAt()
        );
    }
}
