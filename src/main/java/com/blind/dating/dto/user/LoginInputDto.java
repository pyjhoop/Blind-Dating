package com.blind.dating.dto.user;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
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
