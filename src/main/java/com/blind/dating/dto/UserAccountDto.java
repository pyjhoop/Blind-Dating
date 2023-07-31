package com.blind.dating.dto;

import com.blind.dating.domain.UserAccount;
import lombok.*;

@Setter
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class UserAccountDto {

    private String userId;
    private String userPassword;
    private String nickname;
    private String region;
    private int score;
    private String mbti;
    private String gender;
    private Boolean deleted;

    public static UserAccountDto of(String userId, String userPassword, String nickname, String region, int score, String mbti, String gender, Boolean deleted) {
        return new UserAccountDto(userId,userPassword, nickname, region, score, mbti, gender, deleted);
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
                gender,
                deleted
        );
    }
}
