package com.blind.dating.common.code;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum CommentResponseCode implements ResponseCode{
    COMMENT_CREATE_SUCCESS(200,"댓글이 생성되었습니다.","OK"),
    COMMENT_UPDATE_SUCCESS(200,"댓글이 수정되었습니다.","OK"),
    COMMENT_DELETE_SUCCESS(200,"댓글이 삭제되었습니다.","OK"),
    COMMENT_GET_FAIL(404,"댓글이 존재하지 않습니다.","Bad Request");

    private final int code;
    private final String message;
    private final String status;
}
