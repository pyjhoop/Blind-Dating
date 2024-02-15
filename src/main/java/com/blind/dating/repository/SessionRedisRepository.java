package com.blind.dating.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Repository;

import java.util.Set;

@RequiredArgsConstructor
@Repository
public class SessionRedisRepository {
    private final RedisTemplate redisTemplate;

    public void saveUserId(String roomId, String userId){
        System.out.println("시작");
        redisTemplate.opsForSet().add("roomId"+roomId, userId);
    }

    public void removeUserId(String roomId, String userId){
        System.out.println(roomId+ userId+"시작");
        redisTemplate.opsForSet().remove("roomId"+roomId, userId);
    }

    public Set<String> getUsers(String roomId){
        SetOperations<String, String> value = redisTemplate.opsForSet();
        return value.members("roomId"+roomId);

    }

}
