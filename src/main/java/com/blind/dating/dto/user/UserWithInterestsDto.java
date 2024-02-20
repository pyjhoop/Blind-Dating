package com.blind.dating.dto.user;

import com.blind.dating.domain.user.UserAccount;
import com.blind.dating.dto.interest.InterestDto;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
public class UserWithInterestsDto {

    private Long id;
    private String email;
    private String nickname;
    private String region;
    private String mbti;
    private String gender;
    private List<InterestDto> interests;
    private String selfIntroduction;

private UserWithInterestsDto(Long id, String email, String nickname, String region, String mbti, String gender, List<InterestDto> interests, String selfIntroduction) {
        this.id = id;
        this.email = email;
        this.nickname = nickname;
        this.region = region;
        this.mbti = mbti;
        this.gender = gender;
        this.interests = interests;
        this.selfIntroduction = selfIntroduction;
    }
    public static UserWithInterestsDto of(Long id, String email, String nickname, String region, String mbti, String gender, List<InterestDto> interests , String selfIntroduction) {
        return new UserWithInterestsDto(id, email, nickname, region, mbti, gender, interests, selfIntroduction);
    }

    public static UserWithInterestsDto from(UserAccount user) {
        return new UserWithInterestsDto(
                user.getId(),
                user.getEmail(),
                user.getNickname(),
                user.getRegion(),
                user.getMbti(),
                user.getGender(),
                user.getInterests().stream().map(InterestDto::from).collect(Collectors.toList()),
                user.getSelfIntroduction()
        );
    }



}
