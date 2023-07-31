package com.blind.dating.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageInput {
    private Long receiver;
    private String messageContent;


}
