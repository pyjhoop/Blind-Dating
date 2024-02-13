package com.blind.dating.common.code;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ChattingRoomResponseCode implements ResponseCode{

    GET_ROOMS_SUCCESS(200,"채팅방 리스트가 성공적으로 조회되었습니다.","OK"),
    GET_ROOMS_FAIL(400,"채팅방 리스트 조회에 실패했습니다.","Bad Request"),
    GET_CHATS_SUCCESS(200, "채팅 메세지가 성공적으로 조회되었습니다.", "OK");

    private final int code;
    private final String message;
    private final String status;
}
