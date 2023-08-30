package com.blind.dating.service;

import com.blind.dating.dto.chat.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RedisPublisher {

    private final RedisTemplate<String, Object> redisTemplate;

    public void publish(ChannelTopic topic, ChatDto message){
        redisTemplate.convertAndSend(topic.getTopic(), message);
    }

    public void publicRooms(ChannelTopic topic, ChatListWithUserId rooms){
        redisTemplate.convertAndSend(topic.getTopic(), rooms);
    }
}