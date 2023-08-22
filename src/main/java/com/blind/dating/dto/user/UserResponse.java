package com.blind.dating.dto.user;

import com.blind.dating.domain.UserAccount;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@Builder
public class UserResponse {

    private TokenDto token;
    private Long id;
    private String nickname;


    protected UserResponse(TokenDto token,Long id, String nickname) {
        this.token = token;
        this.id = id;
        this.nickname = nickname;
    }

    public static UserResponse of(TokenDto token,Long id, String nickname) {
        return new UserResponse(token,id, nickname);
    }

    public static UserResponse from(UserAccount user, TokenDto tokens){
        return new UserResponse(
                tokens,
                user.getId(),
                user.getNickname()
        );
    }
}
