package com.blind.dating.dto.user;

import com.blind.dating.domain.UserAccount;
import lombok.Data;
import lombok.NoArgsConstructor;

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

    private UserRequestDto(String userId, String userPassword, String nickname, String region, int score, String mbti, String gender) {
        this.userId = userId;
        this.userPassword = userPassword;
        this.nickname = nickname;
        this.region = region;
        this.score = score;
        this.mbti = mbti;
        this.gender = gender;
    }

    public static UserRequestDto of(String userId, String userPassword, String nickname, String region, int score, String mbti, String gender) {
        return new UserRequestDto(userId,userPassword, nickname, region, score, mbti,gender);
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
                entity.getDeleted()
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
                gender
        );
    }

}
