package com.blind.dating.dto.user;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@Builder
public class UserResponse {

    private String accessToken;
    private Long id;
    private String nickname;


    protected UserResponse(String accessToken,Long id, String nickname) {
        this.accessToken = accessToken;
        this.id = id;
        this.nickname = nickname;
    }

    public static UserResponse of(String accessToken,Long id, String nickname) {
        return new UserResponse(accessToken,id, nickname);
    }
}
