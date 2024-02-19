package com.blind.dating.common.code;

import com.blind.dating.common.code.ResponseCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum UserResponseCode implements ResponseCode {
    AUTHORIZE_FAIL(401,"인증에 실패했습니다.","Unauthorization"),
    REGISTER_SUCCESS(200,"회원가입이 성공적으로 진행되었습니다.", "OK"),
    LOGIN_SUCCESS(200,"로그인이 성공적으로 진행되었습니다.","OK"),
    LOGIN_FAIL(404,"로그인에 실패했습니다.","Not Found"),
    LOGOUT_SUCCESS(200,"로그아웃이 성공적으로 진행되었습니다.","OK"),
    EXIST_USER_ID(200,"아이디가 존재합니다.", "false"),
    NOT_EXIST_USER_ID(200,"사용가능한 아이디입니다.", "true"),
    EXIST_NICKNAME(200,"닉네임이 존재합니다.", "false"),
    NOT_EXIST_NICKNAME(200,"사용가능한 닉네임입니다.", "true"),
    GET_USER_LIST_SUCCESS(200,"추천유저가 성공적으로 조회되었습니다.","OK"),
    GET_USER_LIST_FAIL(404,"추천 유저 조회에 실패했습니다.","Bad Request"),
    GET_USER_INFO_SUCCESS(200, "내 정보가 성공적으로 조회되었습니다.","OK"),
    GET_USER_INFO_FAIL(404, "내 정보 조회에 실패했습니다.","Not Found"),
    USER_NOT_FOUND(404, "내 정보 조회에 실패했습니다.","Not Found"),
    UPDATE_USER_INFO_SUCCESS(200,"내 정보가 성공적으로 수정되었습니다.","OK"),
    UPDATE_PROFILE_SUCCESS(200,"프로필이 성공적으로 수정되었습니다.","OK"),
    UPDATE_PROFILE_FAIL(500,"내부 오류로 프로필 수정이 실패했습니다.","Internal Server Error"),
    NOT_MATCH_PASSWORD(400, "비밀번호가 일치하지 않습니다.", "Bad Request"),
    USER_DELETE_SUCCESS(200,"회원 탈퇴 성공", "OK"),
    UPDATE_USER_INFO_FAIL(400,"내 정보 수정중 예외가 발생했습니다..","Bad Request");

    private final int code;
    private final String message;
    private final String status;
}
