package com.blind.dating.domain.user;

import com.blind.dating.domain.BaseEntity;
import com.blind.dating.domain.interest.Interest;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import java.time.LocalDateTime;
import java.util.*;

@Getter
@ToString(callSuper = true)
@Table(indexes = {
        @Index(columnList = "userId"),
        @Index(columnList = "nickname"),
        @Index(columnList = "gender")
})
@Entity
@AllArgsConstructor
public class UserAccount extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "userAccountId")
    private Long id;

    @Setter
    @Column(nullable = false, length = 50, unique = true)
    private String userId;

    @Setter
    private String userPassword;

    @Setter
    @Column(nullable = false, unique = true)
    private String nickname;

    @Setter
    @Column(nullable = false, length = 100)
    private String region;

    @Setter
    @Column(length = 20)
    private String mbti;

    @Setter
    @Column(nullable = false, length = 20)
    private String gender;

    @Setter
    private Boolean deleted;

    @Setter
    private String selfIntroduction;

    @Setter
    private LocalDateTime recentLogin;

    @Setter @Enumerated(EnumType.STRING)
    private Role role;

    @Setter
    private String social;

    @Setter
    private String originProfile;

    @Setter
    private String changedProfile;

    @OneToMany(mappedBy = "userAccount", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @ToString.Exclude @Setter
    private List<Interest> interests;



    protected UserAccount(){}

    private UserAccount(String userId, String userPassword, String nickname, String region, String mbti, String gender,Boolean deleted, String selfIntroduction, LocalDateTime recentLogin, Role role) {
        this.userId = userId;
        this.userPassword = userPassword;
        this.nickname = nickname;
        this.region = region;
        this.mbti = mbti;
        this.gender = gender;
        this.deleted = deleted;
        this.selfIntroduction = selfIntroduction;
        this.recentLogin = recentLogin;
        this.role = role;
    }

    public static UserAccount of(String userId, String userPassword, String nickname, String region, String mbti, String gender, Boolean deleted, String selfIntroduction, LocalDateTime recentLogin, Role role) {
        return new UserAccount(userId, userPassword, nickname, region, mbti, gender,deleted, selfIntroduction, recentLogin, role);
    }

    private UserAccount(String userId, String userPassword, String nickname, String region, String mbti, String gender, String selfIntroduction) {
        this.userId = userId;
        this.userPassword = userPassword;
        this.nickname = nickname;
        this.region = region;
        this.mbti = mbti;
        this.gender = gender;
        this.selfIntroduction = selfIntroduction;
    }
    public static UserAccount of(String userId, String userPassword, String nickname, String region, String mbti, String gender, String selfIntroduction) {
        return new UserAccount(userId, userPassword, nickname, region, mbti, gender, selfIntroduction);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserAccount that = (UserAccount) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}