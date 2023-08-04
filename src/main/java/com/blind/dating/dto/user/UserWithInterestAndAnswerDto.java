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
    private String nickname;
    private String region;
    private String mbti;
    private String gender;
    private Set<InterestDto> interests;
    private Set<AnswerDto> answers;

    private UserWithInterestAndAnswerDto(Long id, String nickname, String region, String mbti, String gender, Set<InterestDto> interests, Set<AnswerDto> answers) {
        this.id = id;
        this.nickname = nickname;
        this.region = region;
        this.mbti = mbti;
        this.gender = gender;
        this.interests = interests;
        this.answers = answers;
    }
    public static UserWithInterestAndAnswerDto of(Long id, String nickname, String region, String mbti, String gender, Set<InterestDto> interests, Set<AnswerDto> answers) {
        return new UserWithInterestAndAnswerDto(id, nickname, region, mbti, gender, interests, answers);
    }

    public static UserWithInterestAndAnswerDto from(UserAccount user) {
        return new UserWithInterestAndAnswerDto(
                user.getId(),
                user.getNickname(),
                user.getRegion(),
                user.getMbti(),
                user.getGender(),
                user.getInterests().stream().map(InterestDto::from).collect(Collectors.toSet()),
                user.getAnswers().stream().map(AnswerDto::from).collect(Collectors.toSet())
        );

    }



}
