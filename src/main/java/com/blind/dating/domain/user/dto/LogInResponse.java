package com.blind.dating.domain.user.dto;

import com.blind.dating.domain.user.UserAccount;
import com.blind.dating.dto.interest.InterestDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LogInResponse {

    private Long id;
    private String email;
    private String nickname;
    private String region;
    private String mbti;
    private String gender;
    private List<InterestDto> interests;
    private String selfIntroduction;
    private String accessToken;
    private String refreshToken;
    private String profile;

    public LogInResponse(Long id, String email, String nickname, String region, String mbti, String gender, List<InterestDto> interests, String selfIntroduction, String profile) {
        this.id = id;
        this.email = email;
        this.nickname = nickname;
        this.region = region;
        this.mbti = mbti;
        this.gender = gender;
        this.interests = interests;
        this.selfIntroduction = selfIntroduction;
        this.profile = profile;
    }

    public static LogInResponse from(UserAccount user, String accessToken, String refreshToken) {
        return new LogInResponse(
                user.getId(),
                user.getEmail(),
                user.getNickname(),
                user.getRegion(),
                user.getMbti(),
                user.getGender(),
                user.getInterests().stream().map(InterestDto::from).collect(Collectors.toList()),
                user.getSelfIntroduction(),
                accessToken,
                refreshToken,
                user.getChangedProfile()
        );
    }
}
