package com.blind.dating.common.code;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum LikesUnlikesResponseCode implements ResponseCode{
    LIKE_SUCCESS(200,"좋아요에 성공했습니다.","OK"),
    UNLIKE_SUCCESS(200,"싫어요에 성공했습니다.","OK");

    private final int code;
    private final String message;
    private final String status;
}
