package com.blind.dating.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
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
public class UserAccount extends BaseEntity implements UserDetails {

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
    @Column(nullable = true, length = 20)
    private String mbti;

    @Setter
    @Column(nullable = false, length = 20)
    private String gender;

    @Setter
    @Column(nullable = true)
    private Boolean deleted;

    @Setter
    @Column(nullable = false)
    private String refreshToken;

    @Setter
    private String selfIntroduction;

    @Setter
    private LocalDateTime recentLogin;


    @OneToMany(mappedBy = "userAccount", fetch = FetchType.EAGER)
    @ToString.Exclude
    private final Set<Interest> interests = new LinkedHashSet<>();

    @OneToMany(mappedBy = "userAccount",fetch = FetchType.EAGER)
    @ToString.Exclude
    private final Set<Question> questions = new LinkedHashSet<>();

    @OneToMany(mappedBy = "receiver",fetch = FetchType.EAGER)
    @ToString.Exclude
    private final Set<LikesUnlikes> likesUnlikes = new LinkedHashSet<>();


    protected UserAccount(){}

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
        if (!(o instanceof UserAccount)) return false;
        UserAccount that = (UserAccount) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public String getPassword() {
        return userPassword;
    }

    @Override
    public String getUsername() {
        return userId;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
