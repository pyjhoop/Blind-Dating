package com.blind.dating.dto.user;

import com.blind.dating.domain.UserAccount;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class UserRequestDto {

    private String userId;
    private String userPassword;
    private String nickname;
    private String region;
    private int score;
    private String mbti;
    private String gender;
    private List<String> interests;
    private String selfIntroduction;

    private UserRequestDto(String userId, String userPassword, String nickname, String region, int score, String mbti, String gender, String selfIntroduction) {
        this.userId = userId;
        this.userPassword = userPassword;
        this.nickname = nickname;
        this.region = region;
        this.score = score;
        this.mbti = mbti;
        this.gender = gender;
        this.selfIntroduction = selfIntroduction;
    }

    public static UserRequestDto of(String userId, String userPassword, String nickname, String region, int score, String mbti, String gender, String selfIntroduction) {
        return new UserRequestDto(userId,userPassword, nickname, region, score, mbti,gender, selfIntroduction);
    }

    public static UserAccountDto from(UserAccount entity){
        return new UserAccountDto(
                entity.getUserId(),
                entity.getUserPassword(),
                entity.getNickname(),
                entity.getRegion(),
                entity.getScore(),
                entity.getMbti(),
                entity.getGender(),
                entity.getDeleted(),
                entity.getSelfIntroduction()
        );
    }

    public UserAccount toEntity(){
        return UserAccount.of(
                userId,
                userPassword,
                nickname,
                region,
                score,
                mbti,
                gender,
                selfIntroduction
        );
    }

}
