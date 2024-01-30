package com.blind.dating.common.code;

import com.blind.dating.common.code.ResponseCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum UserResponseCode implements ResponseCode {
    REGISTER_SUCCESS(200,"회원가입이 성공적으로 진행되었습니다.", "OK"),
    LOGIN_SUCCESS(200,"로그인이 성공적으로 진행되었습니다.","OK"),
    LOGOUT_SUCCESS(200,"로그아웃이 성공적으로 진행되었습니다.","OK"),
    EXIST_USER_ID(200,"아이디가 존재합니다.", "false"),
    NOT_EXIST_USER_ID(200,"사용가능한 아이디입니다.", "true"),
    EXIST_NICKNAME(200,"닉네임이 존재합니다.", "false"),
    NOT_EXIST_NICKNAME(200,"사용가능한 닉네임입니다.", "true"),
    GET_USER_LIST_SUCCESS(200,"추천유저가 성공적으로 조회되었습니다.","OK"),
    GET_USER_INFO_SUCCESS(200, "내 정보가 성공적으로 조회되었습니다.","OK"),
    UPDATE_USER_INFO_SUCCESS(200,"내 정보가 성공적으로 수정되었습니다.","OK");

    private final int code;
    private final String message;
    private final String status;
}
