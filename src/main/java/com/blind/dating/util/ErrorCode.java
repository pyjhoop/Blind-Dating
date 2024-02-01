package com.blind.dating.util;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    WRONG_TYPE_TOKEN("401","유효하지 않은 토큰입니다."),
    EXPIRED_TOKEN("401","만료된 토큰입니다."),
    UNSUPPORTED_TOKEN("401","지원되지 않는 토큰입니다."),
    MALFORMED_TOKEN("401","잘못된 JWT 서명입니다.");


    private String code;
    private String message;
}