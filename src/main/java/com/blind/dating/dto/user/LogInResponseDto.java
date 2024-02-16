package com.blind.dating.dto.user;

import com.blind.dating.dto.interest.InterestDto;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class LogInResponseDto {

    private Long id;
    private String userId;
    private String nickname;
    private String region;
    private String mbti;
    private String gender;
    private List<InterestDto> interests;
    private String selfIntroduction;
    private String accessToken;

    private LogInResponseDto(Long id, String userId, String nickname, String region, String mbti, String gender, List<InterestDto> interests, String selfIntroduction, String accessToken) {
        this.id = id;
        this.userId = userId;
        this.nickname = nickname;
        this.region = region;
        this.mbti = mbti;
        this.gender = gender;
        this.interests = interests;
        this.selfIntroduction = selfIntroduction;
        this.accessToken = accessToken;
    }

    public static LogInResponseDto from(LogInResponse response) {
        return new LogInResponseDto(
                response.getId(),
                response.getUserId(),
                response.getNickname(),
                response.getRegion(),
                response.getMbti(),
                response.getGender(),
                response.getInterests(),
                response.getSelfIntroduction(),
                response.getAccessToken()
        );
    }
}
