package com.blind.dating.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Post extends BaseEntity{

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
