package com.blind.dating.common.code;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum MessageResponseCode implements ResponseCode{
    POST_MESSAGE_FAIL_WITH_USER_ID(404,"메시지 생성 중 유저 정보를 찾을 수 없습니다.", "Not Fount"),
    MESSAGE_NOT_FOUNT(404,"존재하지 않는 메시지입니다.", "Not Fount"),
    ACCEPT_MESSAGE_SUCCESS(200,"메시지를 수락해 채팅방이 성공적으로 생성되었습니다.", "OK"),
    REJECT_MESSAGE_SUCCESS(200,"메시지 거절에 성공했습니다.", "OK"),
    GET_MESSAGE_TOME_SUCCESS(200,"내게온 메시지 조회 성공했습니다.", "OK"),
    GET_MESSAGE_FROMME_SUCCESS(200,"내가 보낸 메시지 조회 성공했습니다.", "OK"),
    POST_MESSAGE_SUCCESS(200,"메시지기 성공적으로 전송되었습니다.", "OK"),
    MESSAGE_ALREADY_POST(200,"해당 유저에게 이미 메시지를 전송했습니다.", "OK");

    private final int code;
    private final String message;
    private final String status;
}
