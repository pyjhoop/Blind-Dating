package com.blind.dating.dto;

import lombok.Getter;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import javax.persistence.Id;

@Getter
public class RefreshToken {

    @Id
    private String refreshToken;

    private Long userId;

    public RefreshToken(String refreshToken, Long userId) {
        this.refreshToken = refreshToken;
        this.userId = userId;
    }

    public String getRefreshToken(){
        return refreshToken;
    }

    public Long getUserId(){
        return userId;
    }
}
