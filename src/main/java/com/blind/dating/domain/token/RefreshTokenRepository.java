package com.blind.dating.domain.token;

import com.blind.dating.dto.RefreshToken;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Repository;

import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
@Repository
public class RefreshTokenRepository {

    private final RedisTemplate redisTemplate;


    public String save(String userId, String refreshToken){
        ValueOperations<String , String> value = redisTemplate.opsForValue();
        value.set(userId, refreshToken);
        redisTemplate.expire(userId, 7L, TimeUnit.DAYS);
        return userId;
    }

    public String getRefreshToken(String userId){
        ValueOperations<String, String> value = redisTemplate.opsForValue();
        String refreshToken = value.get(userId);
        return refreshToken;
    }

    public void deleteRefreshToken(String userId){
        redisTemplate.delete(userId);
    }

}
