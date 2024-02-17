package com.blind.dating.dto.user;

import com.blind.dating.domain.user.UserAccount;
import com.blind.dating.dto.interest.InterestDto;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
public class UserWithInterestDto {

    private Long id;
    private String nickname;
    private String region;
    private String mbti;
    private String gender;
    Set<InterestDto> interests;

    private UserWithInterestDto(Long id, String nickname, String region, String mbti,String gender, Set<InterestDto> interests) {
        this.id = id;
        this.nickname = nickname;
        this.region = region;
        this.mbti = mbti;
        this.gender = gender;
        this.interests = interests;
    }

    public static UserWithInterestDto of(Long id, String nickname, String region, String mbti,String gender, Set<InterestDto> interests) {
        return new UserWithInterestDto(id,nickname, region, mbti, gender, interests);
    }

    public static UserWithInterestDto from(UserAccount userAccount) {
        return new UserWithInterestDto(
                userAccount.getId(),
                userAccount.getNickname(),
                userAccount.getRegion(),
                userAccount.getMbti(),
                userAccount.getGender(),
                userAccount.getInterests().stream().map(InterestDto::from).collect(Collectors.toSet())
        );
    }

}
