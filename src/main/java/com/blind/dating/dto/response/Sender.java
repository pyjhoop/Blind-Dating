package com.blind.dating.dto.response;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Sender {
    private Long senderId;
    private String nickname;

    private Sender(Long senderId, String nickname) {
        this.senderId = senderId;
        this.nickname = nickname;
    }

    public static Sender of(Long senderId, String nickname) {
        return new Sender(senderId, nickname);
    }

    public static Sender from(Message message){
       return new Sender(message.getSender().getId(), message.getSender().getNickname());
    }
}
