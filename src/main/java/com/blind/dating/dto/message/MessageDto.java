package com.blind.dating.dto.message;

import com.blind.dating.domain.Message;
import com.blind.dating.dto.response.Sender;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class MessageDto {
    private Long id;
    private Sender sender;
    private String messageContent;
    private String status;

    protected MessageDto(Long id, Sender sender, Long receiver, String messageContent, String status) {
        this.id = id;
        this.sender = sender;
        this.messageContent = messageContent;
        this.status = status;
    }

    public static MessageDto of(Long id, Sender sender, Long receiver, String messageContent, String status) {
        return new MessageDto(id,sender, receiver, messageContent, status);
    }

    public static MessageDto from(Message message){
        return new MessageDto(
                message.getId(),
                Sender.from(message),
                message.getReceiver(),
                message.getMessageContent(),
                message.getStatus()
        );
    }

}
