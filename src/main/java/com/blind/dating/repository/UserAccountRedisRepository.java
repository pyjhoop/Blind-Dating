package com.blind.dating.repository;

import com.blind.dating.dto.user.UserIdWithNicknameAndGender;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Repository;

import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
@Repository
public class UserAccountRedisRepository {

    private final RedisTemplate redisTemplate;

    public void saveUser(Long userId, UserIdWithNicknameAndGender dto){
        ValueOperations<String, UserIdWithNicknameAndGender> value = redisTemplate.opsForValue();
        value.set("user"+userId, dto);
        redisTemplate.expire("user"+userId, 1L, TimeUnit.DAYS);
    }


}
