package com.blind.dating.dto.message;

import com.blind.dating.domain.Message;
import com.blind.dating.domain.UserAccount;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MessageRequestDto {
    private Long receiverId;
    private String message;

    public Message toEntity(UserAccount user) {
        return Message.of(user,receiverId, message, MessageStatus.WAIT);
    }
}
