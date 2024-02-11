package com.blind.dating.common.code;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PostResponseCode implements ResponseCode{
    CREATE_POST_SUCCESS(200,"게시글이 성공적으로 생성되었습니다.", "OK"),
    CREATE_POST_FAil(400,"게시글 생성에 실패했습니다. 다시 시도해 주세요", "OK"),
    UPDATE_POST_SUCCESS(200,"게시글이 성공적으로 수정되었습니다.", "OK"),
    GET_POST_SUCCESS(200,"게시글이 성공적으로 조회되었습니다.", "OK"),
    POST_NOT_FOUNT(404,"게시글을 찾을 수 없습니다.", "Not Fount");

    private final int code;
    private final String message;
    private final String status;
}
