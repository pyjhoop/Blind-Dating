package com.blind.dating.domain.post;

import com.blind.dating.domain.BaseEntity;
import com.blind.dating.domain.comment.Comment;
import com.blind.dating.domain.user.UserAccount;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Post extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne @Setter
    private UserAccount user;

    @Column(nullable = false) @Setter
    private String title;

    @Column(nullable = false) @Lob @Setter
    private String content;

    private Long hit;

    private Long view;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @ToString.Exclude
    private List<Comment> comments;

    public void increaseHit() {
        this.hit++;
    }

    public void increaseView() {
        this.view++;
    }

    public Post(UserAccount user, String title, String content, Long hit, Long view) {
        this.user = user;
        this.title = title;
        this.content = content;
        this.hit = hit;
        this.view = view;
    }
}
