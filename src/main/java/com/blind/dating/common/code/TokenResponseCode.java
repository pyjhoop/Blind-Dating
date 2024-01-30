package com.blind.dating.common.code;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum TokenResponseCode implements ResponseCode{
    ISSUE_ACCESS_TOKEN_SUCCESS(200,"accessToken이 성공적으로 생성되었습니다.", "OK");

    private final int code;
    private final String message;
    private final String status;
}
