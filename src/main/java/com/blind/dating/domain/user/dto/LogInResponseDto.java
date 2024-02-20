package com.blind.dating.domain.user.dto;

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
    private String profile;

    private LogInResponseDto(Long id, String userId, String nickname, String region, String mbti, String gender, List<InterestDto> interests, String selfIntroduction, String accessToken, String profile) {
        this.id = id;
        this.userId = userId;
        this.nickname = nickname;
        this.region = region;
        this.mbti = mbti;
        this.gender = gender;
        this.interests = interests;
        this.selfIntroduction = selfIntroduction;
        this.accessToken = accessToken;
        this.profile = profile;
    }

    public static LogInResponseDto from(LogInResponse response) {
        return new LogInResponseDto(
                response.getId(),
                response.getEmail(),
                response.getNickname(),
                response.getRegion(),
                response.getMbti(),
                response.getGender(),
                response.getInterests(),
                response.getSelfIntroduction(),
                response.getAccessToken(),
                response.getProfile()
        );
    }
}
