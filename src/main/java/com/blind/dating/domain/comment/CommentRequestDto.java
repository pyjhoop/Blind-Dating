package com.blind.dating.domain.comment;

public record CommentRequestDto(
        Long postId,
        String content
) {
}
