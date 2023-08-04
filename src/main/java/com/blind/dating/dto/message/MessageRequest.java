package com.blind.dating.dto.message;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageRequest {
    private Long receiverId;
    private String messageContent;

}
