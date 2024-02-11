package com.blind.dating.dto.post;

import com.blind.dating.domain.Post;
import com.blind.dating.domain.UserAccount;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostResponseDto {

    private Long id;

    private UserInfo user;

    private String title;

    private String content;

    private Long hit;

    private Long view;



    public static PostResponseDto From(Post post) {
        return new PostResponseDto(
                post.getId(),
                new UserInfo(post.getUser().getId(), post.getUser().getNickname()),
                post.getTitle(),
                post.getContent(),
                post.getHit(),
                post.getView()
        );
    }
}
