package com.blind.dating.common.code;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ChatResponseCode implements ResponseCode{
    CHAT_NOT_FOUND(404,"해당 채팅이 존재하지 않습니다.", "Bad Request"),
    CHAT_SEND_FAIL(500,"채팅 전송시 문제가 발생했습니다.", "서버오류"),
    READ_CHAT_NOT_FOUND(404,"읽은 채팅이 존재하지 않습니다.", "Bad Request");

    private final int code;
    private final String message;
    private final String status;
}
