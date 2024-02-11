package com.blind.dating.dto.post;

import com.blind.dating.domain.Post;
import com.blind.dating.domain.UserAccount;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostRequestDto {
    private String title;

    private String content;

    public Post toEntity(UserAccount user) {
        return new Post(user, this.title, this.content, 0L,0L);
    }
}
