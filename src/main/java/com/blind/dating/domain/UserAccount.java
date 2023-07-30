package com.blind.dating.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

@Getter
@ToString(callSuper = true)
@Table(indexes = {
        @Index(columnList = "userId"),
        @Index(columnList = "nickname"),
        @Index(columnList = "gender")
})
@Entity
public class UserAccount extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @Column(nullable = false, length = 50, unique = true)
    private String userId;

    @Setter
    @Column(nullable = false, length = 255)
    private String userPassword;

    @Setter
    @Column(nullable = false, unique = true, length = 255)
    private String nickname;

    @Setter
    @Column(nullable = false, length = 100)
    private String region;

    @Setter
    @Column(nullable = true)
    private int score;

    @Setter
    @Column(nullable = true, length = 20)
    private String mbti;

    @Setter
    @Column(nullable = false, length = 20)
    private String gender;

    @Setter
    @Column(nullable = true)
    private Boolean deleted;

    @OrderBy("createdAt DESC")
    @OneToMany(mappedBy = "sender", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @ToString.Exclude
    private final Set<Message> messages = new LinkedHashSet<>();

    @OneToMany(mappedBy = "userAccount", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @ToString.Exclude
    private final Set<Interest> interests = new LinkedHashSet<>();




    public UserAccount(){}

    public UserAccount(String userId, String userPassword, String nickname, String region, int score, String mbti, String gender, boolean deleted) {
        this.userId = userId;
        this.userPassword = userPassword;
        this.nickname = nickname;
        this.region = region;
        this.score = score;
        this.mbti = mbti;
        this.gender = gender;
        this.deleted = deleted;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserAccount)) return false;
        UserAccount that = (UserAccount) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
