package com.blind.dating.dto;

import com.blind.dating.domain.Message;
import com.blind.dating.domain.UserAccount;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@ToString
@Builder
public class MessageDto {
    private Long id;
    private UserAccount sender;
    private Long receiver;
    private String messageContent;
    private String status;

    protected MessageDto(Long id, UserAccount sender, Long receiver, String messageContent, String status) {
        this.id = id;
        this.sender = sender;
        this.receiver = receiver;
        this.messageContent = messageContent;
        this.status = status;
    }

    public static MessageDto of(Long id, UserAccount sender, Long receiver, String messageContent, String status) {
        return new MessageDto(id,sender, receiver, messageContent, status);
    }

    public static MessageDto from(Message message){
        return new MessageDto(
                message.getId(),
                message.getSender(),
                message.getReceiver(),
                message.getMessageContent(),
                message.getStatus()
        );
    }

    public Message toEntity(){
        return Message.of(sender,receiver,messageContent,status);

    }
}
