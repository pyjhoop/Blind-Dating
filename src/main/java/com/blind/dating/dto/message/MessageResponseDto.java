package com.blind.dating.dto.message;

import com.blind.dating.domain.Message;
import com.blind.dating.dto.user.UserInfoDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MessageResponseDto {
    private Long id;
    private Long userId;
    private String userNickname;
    private String content;
    private MessageStatus status;


    public static MessageResponseDto From(Message message) {
        return new MessageResponseDto(message.getId()
                ,message.getUserAccount().getId()
                ,message.getUserAccount().getNickname()
                ,message.getContent()
                ,message.getStatus());
    }
}
