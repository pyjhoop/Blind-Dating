package com.blind.dating.dto.request;

import com.blind.dating.domain.UserAccount;
import com.blind.dating.dto.UserAccountDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Setter
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "UserAccountDto", description = "유저 정보 Dto")
public class UserAccountRequestDto {

    @Schema(description = "유저 아이디")
    private String userId;
    @Schema(description = "유저 비밀번호")
    private String userPassword;
    @Schema(description = "닉네임")
    private String nickname;
    @Schema(description = "사는 지역")
    private String region;
    @Schema(description = "가중치")
    private int score;
    @Schema(description = "MBTI")
    private String mbti;
    @Schema(description = "성별")
    private String gender;


    public static UserAccountRequestDto of(String userId, String userPassword, String nickname, String region, int score, String mbti, String gender) {
        return new UserAccountRequestDto(userId,userPassword, nickname, region, score, mbti, gender);
    }

    public static UserAccountRequestDto from(UserAccount entity){
        return new UserAccountRequestDto(
                entity.getUserId(),
                entity.getUserPassword(),
                entity.getNickname(),
                entity.getRegion(),
                entity.getScore(),
                entity.getMbti(),
                entity.getGender()
        );
    }

    public  UserAccountDto toUserAccountDto(){
        return UserAccountDto.of(userId,userPassword, nickname, region, score, mbti,gender);
    }

}
