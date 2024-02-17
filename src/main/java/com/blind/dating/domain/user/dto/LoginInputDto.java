package com.blind.dating.domain.user.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@ToString
public class LoginInputDto {
    private String userId;
    private String userPassword;

    @Builder
    public LoginInputDto(String userId, String userPassword) {
        this.userId = userId;
        this.userPassword = userPassword;
    }


}
