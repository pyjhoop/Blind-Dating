package com.blind.dating.service;

import com.blind.dating.dto.chat.ChatDto;
import com.blind.dating.dto.chat.ChatListWithUserId;
import com.blind.dating.dto.chat.ChatRoomDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@DisplayName("RedisPublisher - 테스트")
@ExtendWith(MockitoExtension.class)
class RedisPublisherTest {

    @Mock private RedisTemplate<String, Object> redisTemplate;
    @InjectMocks private RedisPublisher redisPublisher;


    @Test
    @DisplayName("채팅방에 메세지 전달하기 - 테스트")
    void givenChannelTopicAndMessage_whenSendMessage_thenSuccess() {
        // Given
        ChannelTopic topic = new ChannelTopic("Topic");
        ChatDto dto = new ChatDto(1L, 1L, 1L, "메세지", LocalDateTime.now(), LocalDateTime.now());

        // When
        redisPublisher.publish(topic, dto);

        verify(redisTemplate, times(1)).convertAndSend(topic.getTopic(), dto);
    }

    @Test
    @DisplayName("채팅방리스트 업데이트 - 테스트")
    void givenChannelTopicAndChatList_whenSendMessage_thenSuccess() {
        // Given
        ChannelTopic topic = new ChannelTopic("Topic");
        ChatListWithUserId list = new ChatListWithUserId(1L, List.of(new ChatRoomDto(), new ChatRoomDto()));

        // When
        redisPublisher.publicRooms(topic, list);

        verify(redisTemplate, times(1)).convertAndSend(topic.getTopic(), list);
    }

}