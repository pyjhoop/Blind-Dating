package com.blind.dating.dto.user;

import com.blind.dating.domain.UserAccount;
import com.blind.dating.dto.interest.InterestDto;
import com.blind.dating.dto.question.QuestionDto;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
public class UserWithInterestAndQuestionDto {

    private Long id;
    private String userId;
    private String nickname;
    private String region;
    private String mbti;
    private String gender;
    private List<InterestDto> interests;
    private List<QuestionDto> questions;
    private String selfIntroduction;

private UserWithInterestAndQuestionDto(Long id, String userId, String nickname, String region, String mbti, String gender, List<InterestDto> interests, List<QuestionDto> questions, String selfIntroduction) {
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
    public static UserWithInterestAndQuestionDto of(Long id, String userId, String nickname, String region, String mbti, String gender, List<InterestDto> interests, List<QuestionDto> questions, String selfIntroduction) {
        return new UserWithInterestAndQuestionDto(id, userId, nickname, region, mbti, gender, interests, questions, selfIntroduction);
    }

    public static UserWithInterestAndQuestionDto from(UserAccount user) {
        return new UserWithInterestAndQuestionDto(
                user.getId(),
                user.getUserId(),
                user.getNickname(),
                user.getRegion(),
                user.getMbti(),
                user.getGender(),
                user.getInterests().stream().map(InterestDto::from).collect(Collectors.toList()),
                user.getQuestions().stream().map(QuestionDto::from).collect(Collectors.toList()),
                user.getSelfIntroduction()
        );

    }



}
