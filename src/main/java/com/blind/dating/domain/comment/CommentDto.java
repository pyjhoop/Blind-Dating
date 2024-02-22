package com.blind.dating.domain.comment;

import java.time.LocalDateTime;

public record CommentDto(
        Long id,
        String content,
        Long writerId,
        String writerNickname,
        LocalDateTime createdAt
) {

    public static CommentDto from(Comment comment) {
        return new CommentDto(comment.getId(),
                comment.getContent(),
                comment.getUserAccount().getId(),
                comment.getUserAccount().getNickname(),
                comment.getCreatedAt());
    }
}
