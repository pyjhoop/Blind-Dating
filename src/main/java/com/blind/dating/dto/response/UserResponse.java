package com.blind.dating.dto.response;

import com.blind.dating.domain.UserAccount;
import com.blind.dating.dto.MessageDto;
import com.blind.dating.dto.TokenDto;
import lombok.*;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
@ToString
@NoArgsConstructor
@Builder
public class UserResponse {

    private TokenDto token;
    private Long id;
    private String nickname;
    private String mbti;
    private String gender;

    protected UserResponse(TokenDto token, Long id, String nickname, String mbti, String gender) {
        this.token = token;
        this.id = id;
        this.nickname = nickname;
        this.mbti = mbti;
        this.gender = gender;
    }

    public static UserResponse of(TokenDto token, Long id, String nickname, String mbti, String gender) {
        return new UserResponse(token, id, nickname, mbti, gender);
    }

    public static UserResponse from(UserAccount user, String token){
        return new UserResponse(
                new TokenDto(token),
                user.getId(),
                user.getNickname(),
                user.getMbti(),
                user.getGender()
        );
    }
}
