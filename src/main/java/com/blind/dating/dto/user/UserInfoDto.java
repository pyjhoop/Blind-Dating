package com.blind.dating.dto.user;

import com.blind.dating.domain.UserAccount;
import com.blind.dating.dto.interest.InterestDto;
import com.blind.dating.dto.question.QuestionDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
public class UserInfoDto {
    private Long id;
    private String nickname;
    private String region;
    private String mbti;
    private String gender;
    private List<InterestDto> interests;
    private List<QuestionDto> questions;
    private String selfIntroduction;

    public UserInfoDto(Long id, String nickname, String region, String mbti, String gender, List<InterestDto> interests, List<QuestionDto> questions, String selfIntroduction) {
        this.id = id;
        this.nickname = nickname;
        this.region = region;
        this.mbti = mbti;
        this.gender = gender;
        this.interests = interests;
        this.questions = questions;
        this.selfIntroduction = selfIntroduction;
    }

    public static UserInfoDto From(UserAccount user) {
        return new UserInfoDto(
                user.getId(),
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
