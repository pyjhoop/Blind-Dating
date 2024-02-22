package com.blind.dating.domain.post;

import com.blind.dating.domain.comment.CommentDto;
import com.blind.dating.dto.post.UserInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostResponseWithCommentDto {

    private Long id;

    private UserInfo user;

    private String title;

    private String content;

    private Long hit;

    private Long view;
    private LocalDate createdAt;
    private List<CommentDto> comments;



    public static PostResponseWithCommentDto From(Post post) {
        return new PostResponseWithCommentDto(
                post.getId(),
                new UserInfo(post.getUser().getId(), post.getUser().getNickname()),
                post.getTitle(),
                post.getContent(),
                post.getHit(),
                post.getView(),
                post.getCreatedAt().toLocalDate(),
                post.getComments().stream().map(CommentDto::from).toList()
        );
    }
}
