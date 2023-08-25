package com.blind.dating.dto.user;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserInfoWithTokens {

    private String accessToken;
    private String refreshToken;
    private Long id;
    private String nickname;
}
