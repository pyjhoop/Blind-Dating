package com.blind.dating.domain.user.dto;

import com.blind.dating.domain.user.UserAccount;
import com.blind.dating.dto.interest.InterestDto;

import java.util.List;
import java.util.stream.Collectors;

public record UserInfo(
        Long id,
        String nickname,
        String region,
        String mbti,
        String gender,
        List<InterestDto> interests,
        String selfIntroduction,
        String profile
) {
    public static UserInfo from(UserAccount user) {
        return new UserInfo(
                user.getId(),
                user.getNickname(),
                user.getRegion(),
                user.getMbti(),
                user.getGender(),
                user.getInterests().stream().map(InterestDto::from).collect(Collectors.toList()),
                user.getSelfIntroduction(),
                user.getChangedProfile()
        );
    }
}
