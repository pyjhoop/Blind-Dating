package com.blind.dating.dto.user;

import com.blind.dating.domain.user.UserAccount;
import lombok.*;

@Setter
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class UserAccountDto {

    private String email;
    private String userPassword;
    private String nickname;
    private String region;
    private String mbti;
    private String gender;
    private Boolean deleted;
    private String selfIntroduction;


    private UserAccountDto(String email, String userPassword, String nickname, String region, String mbti, String gender) {
        this.email = email;
        this.userPassword = userPassword;
        this.nickname = nickname;
        this.region = region;
        this.mbti = mbti;
        this.gender = gender;
    }

    public static UserAccountDto of(String email, String userPassword, String nickname, String region, String mbti, String gender, Boolean deleted, String selfIntroduction) {
        return new UserAccountDto(email,userPassword, nickname, region, mbti, gender, deleted, selfIntroduction);
    }

    public static UserAccountDto of(String email, String userPassword, String nickname, String region, String mbti, String gender) {
        return new UserAccountDto(email,userPassword, nickname, region, mbti, gender);
    }

    public static UserAccountDto from(UserAccount entity){
        return new UserAccountDto(
                entity.getEmail(),
                entity.getUserPassword(),
                entity.getNickname(),
                entity.getRegion(),
                entity.getMbti(),
                entity.getGender(),
                entity.getDeleted(),
                entity.getSelfIntroduction()
        );
    }

    public UserAccount toEntity(){
        return UserAccount.of(
                email,
                userPassword,
                nickname,
                region,
                mbti,
                gender,
                selfIntroduction
        );
    }
}
