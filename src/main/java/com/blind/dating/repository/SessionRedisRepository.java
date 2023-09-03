package com.blind.dating.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Repository;

import java.util.Set;

@RequiredArgsConstructor
@Repository
public class SessionRedisRepository {
    private final RedisTemplate redisTemplate;

    public void saveUserId(String roomId, String userId){
        redisTemplate.opsForSet().add("roomId"+roomId, userId);
    }

    public void removeUserId(String roomId, String userId){
        redisTemplate.opsForSet().remove("roomId"+roomId, userId);
    }

    public Set<String> getUsers(String roomId){
        ValueOperations<String, Set<String>> value = redisTemplate.opsForValue();
        return value.get("roomId"+roomId);

    }

}
