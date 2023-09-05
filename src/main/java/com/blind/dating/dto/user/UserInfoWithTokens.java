package com.blind.dating.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserInfoWithTokens {

    private String accessToken;
    private String refreshToken;
    private Long id;
    private String nickname;
}
