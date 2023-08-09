package com.blind.dating.dto.user;

import com.blind.dating.domain.UserAccount;
import com.blind.dating.dto.answer.AnswerDto;
import com.blind.dating.dto.interest.InterestDto;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
public class UserWithInterestAndAnswerDto {

    private Long id;
    private String userId;
    private String nickname;
    private String region;
    private String mbti;
    private String gender;
    private Set<InterestDto> interests;
    private Set<AnswerDto> answers;
    private String selfIntroduction;

    private UserWithInterestAndAnswerDto(Long id, String userId, String nickname, String region, String mbti, String gender, Set<InterestDto> interests, Set<AnswerDto> answers, String selfIntroduction) {
        this.id = id;
        this.userId = userId;
        this.nickname = nickname;
        this.region = region;
        this.mbti = mbti;
        this.gender = gender;
        this.interests = interests;
        this.answers = answers;
        this.selfIntroduction = selfIntroduction;
    }
    public static UserWithInterestAndAnswerDto of(Long id,String userId, String nickname, String region, String mbti, String gender, Set<InterestDto> interests, Set<AnswerDto> answers, String selfIntroduction) {
        return new UserWithInterestAndAnswerDto(id, userId, nickname, region, mbti, gender, interests, answers, selfIntroduction);
    }

    public static UserWithInterestAndAnswerDto from(UserAccount user) {
        return new UserWithInterestAndAnswerDto(
                user.getId(),
                user.getUserId(),
                user.getNickname(),
                user.getRegion(),
                user.getMbti(),
                user.getGender(),
                user.getInterests().stream().map(InterestDto::from).collect(Collectors.toSet()),
                user.getAnswers().stream().map(AnswerDto::from).collect(Collectors.toSet()),
                user.getSelfIntroduction()
        );

    }



}
