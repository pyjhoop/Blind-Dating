package com.blind.dating.repository;

import com.blind.dating.dto.user.UserIdWithNicknameAndGender;
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

    public UserIdWithNicknameAndGender saveUser(String userId, UserIdWithNicknameAndGender dto){
        ValueOperations<String, UserIdWithNicknameAndGender> value = redisTemplate.opsForValue();
        value.set("user"+userId, dto);
        redisTemplate.expire("user"+userId, 1L, TimeUnit.DAYS);
        return dto;
    }

    public UserIdWithNicknameAndGender getUserInfo(String userId){
        System.out.println(userId);
        ValueOperations<String, UserIdWithNicknameAndGender> value = redisTemplate.opsForValue();
        ObjectMapper mapper = new ObjectMapper();
        UserIdWithNicknameAndGender userInfo = mapper.convertValue(value.get("user"+Long.valueOf(userId)), UserIdWithNicknameAndGender.class);
        return userInfo;
    }


}
