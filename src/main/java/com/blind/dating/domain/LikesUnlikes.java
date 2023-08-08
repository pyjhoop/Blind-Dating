package com.blind.dating.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.util.Objects;

@Getter
@ToString
@NoArgsConstructor
@Entity
public class LikesUnlikes {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @ManyToOne
    private UserAccount userAccount;

    @Setter
    @Column(nullable = false)
    private Long receiverId;

    @Setter
    private Boolean isLike;

    private LikesUnlikes(UserAccount userAccount, Long receiverId, Boolean isLike) {
        this.userAccount = userAccount;
        this.receiverId = receiverId;
        this.isLike = isLike;
    }

    public static LikesUnlikes of(UserAccount userAccount, Long receiverId, Boolean isLike) {
        return new LikesUnlikes(userAccount, receiverId, isLike);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LikesUnlikes)) return false;
        LikesUnlikes that = (LikesUnlikes) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
