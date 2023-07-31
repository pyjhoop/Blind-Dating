package com.blind.dating.dto.response;

import com.blind.dating.dto.MessageDto;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class MessageResponse {
    private Long id;
    private Sender sender;
    private Long receiverId;
    private String messageContent;
    private String status;

    public static MessageResponse of(Long id, Sender sender, Long receiverId, String messageContent, String status) {
        return new MessageResponse(id,sender,receiverId,messageContent,status);
    }

    public static MessageResponse from(MessageDto dto){
        Sender sender = new Sender(dto.getSender().getId(), dto.getSender().getUserId(),dto.getSender().getNickname());
        return new MessageResponse(dto.getId(),sender,dto.getReceiver(),dto.getMessageContent(),dto.getStatus());
    }
}
