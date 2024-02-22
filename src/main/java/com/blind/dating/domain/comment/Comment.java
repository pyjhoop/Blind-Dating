package com.blind.dating.domain.comment;

import com.blind.dating.domain.BaseEntity;
import com.blind.dating.domain.post.Post;
import com.blind.dating.domain.user.UserAccount;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Comment extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    private String content;

    @Setter
    private Boolean status;

    @ManyToOne
    private UserAccount userAccount;
    @ManyToOne
    private Post post;


    private Comment(String content, Boolean status, UserAccount userAccount, Post post) {
        this.content = content;
        this.status = status;
        this.userAccount = userAccount;
        this.post = post;
    }

    public static Comment of(String content, Boolean status, UserAccount userAccount, Post post) {
        return new Comment(content, status ,userAccount, post);
    }
}
