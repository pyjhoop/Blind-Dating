package com.blind.dating.dto.user;

import com.blind.dating.domain.Interest;
import com.blind.dating.domain.UserAccount;
import com.blind.dating.dto.interest.InterestDto;
import com.blind.dating.dto.question.QuestionDto;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
public class LogInResponseDto {

    private Long id;
    private String userId;
    private String nickname;
    private String region;
    private String mbti;
    private String gender;
    private Set<InterestDto> interests;
    private Set<QuestionDto> questions;
    private String selfIntroduction;
    private String accessToken;

    private LogInResponseDto(Long id, String userId, String nickname, String region, String mbti, String gender, Set<InterestDto> interests, Set<QuestionDto> questions, String selfIntroduction) {
        this.id = id;
        this.userId = userId;
        this.nickname = nickname;
        this.region = region;
        this.mbti = mbti;
        this.gender = gender;
        this.interests = interests;
        this.questions = questions;
        this.selfIntroduction = selfIntroduction;
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
                response.getQuestions(),
                response.getSelfIntroduction());
    }
}
