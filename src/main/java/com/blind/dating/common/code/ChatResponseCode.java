package com.blind.dating.common.code;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ChatResponseCode implements ResponseCode{
    CHAT_NOT_FOUND(404,"해당 채팅이 존재하지 않습니다.", "Bad Request"),
    READ_CHAT_NOT_FOUND(404,"읽은 채팅이 존재하지 않습니다.", "Bad Request");

    private final int code;
    private final String message;
    private final String status;
}
