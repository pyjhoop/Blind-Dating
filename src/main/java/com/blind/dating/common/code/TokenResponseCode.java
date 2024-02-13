package com.blind.dating.common.code;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum TokenResponseCode implements ResponseCode{
    ISSUE_ACCESS_TOKEN_SUCCESS(200,"accessToken이 성공적으로 생성되었습니다.", "OK"),
    NOT_FOUND_REFRESH_TOKEN(400,"조회된 리프레쉬 토큰이 없습니다.", "Bad Request"),
    UPDATE_REFRESH_TOKEN_FAIL(400, "잘못된 리프래쉬 토큰입니다.", "Bad Request");


    private final int code;
    private final String message;
    private final String status;
}
