package com.blind.dating.repository;

import com.blind.dating.dto.user.UserIdWithNickname;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Repository;

import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
@Repository
public class UserAccountRedisRepository {

    private final RedisTemplate redisTemplate;

    public void saveUser(String userId, UserIdWithNickname dto){
        ValueOperations<String, UserIdWithNickname> value = redisTemplate.opsForValue();
        value.set("user"+userId,dto);
        redisTemplate.expire("user"+userId, 1L, TimeUnit.DAYS);
    }

    public UserIdWithNickname getUser(String userId){
        ValueOperations<String, UserIdWithNickname> value = redisTemplate.opsForValue();
        ObjectMapper mapper = new ObjectMapper();
        UserIdWithNickname userIdWithNickname = mapper.convertValue(value.get("user"+userId), UserIdWithNickname.class);
        return userIdWithNickname;

    }
}
