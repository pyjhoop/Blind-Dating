package com.blind.dating.service;

import com.blind.dating.dto.chat.ChatDto;
import com.blind.dating.dto.chat.ChatListWithUserId;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.data.redis.core.StringRedisTemplate;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RedisSubscriberTest {

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private StringRedisTemplate redisTemplate;

    @Mock
    private SimpMessageSendingOperations messagingTemplate;

    @InjectMocks
    private RedisSubscriber redisSubscriber;

    @Test
    public void testOnMessageRooms() throws Exception {
        // Given
        Message message = mock(Message.class);
        byte[] pattern = "pattern".getBytes();
        String userId = "123";
        String roomsStr = "{\"userId\":\"" + userId + "\",\"rooms\":[]}";

        when(redisTemplate.getStringSerializer()).thenReturn(StringRedisSerializer.UTF_8);
        when(message.getChannel()).thenReturn("rooms".getBytes());
        when(message.getBody()).thenReturn(roomsStr.getBytes());

        ChatListWithUserId rooms = new ChatListWithUserId();
        rooms.setUserId(Long.valueOf(userId));

        when(objectMapper.readValue(roomsStr, ChatListWithUserId.class)).thenReturn(rooms);

        // When
        redisSubscriber.onMessage(message, pattern);

        // Then
        verify(messagingTemplate, times(1)).convertAndSend("/sub/chatroom/" + userId, rooms);
    }


    @Test
    public void testOnMessageChatRoom() throws Exception {
        // Given
        Message message = mock(Message.class);
        byte[] pattern = "pattern".getBytes();
        String chatRoomId = "456";
        String publishMessage = "{\"chatRoomId\":\"" + chatRoomId + "\",\"message\":\"Hello\"}";

        when(redisTemplate.getStringSerializer()).thenReturn(StringRedisSerializer.UTF_8);
        when(message.getChannel()).thenReturn("chatRoom".getBytes());
        when(message.getBody()).thenReturn(publishMessage.getBytes());

        ChatDto chatMessage = new ChatDto();
        chatMessage.setChatRoomId(Long.valueOf(chatRoomId));
        chatMessage.setMessage("Hello");

        when(objectMapper.readValue(publishMessage, ChatDto.class)).thenReturn(chatMessage);

        // When
        redisSubscriber.onMessage(message, pattern);

        // Then
        verify(messagingTemplate, times(1)).convertAndSend("/sub/chat/room/" + chatRoomId, chatMessage);
    }

    @Test
    public void testOnMessageExceptionHandling() throws Exception {
        // Given
        Message message = mock(Message.class);
        byte[] pattern = "pattern".getBytes();
        String errorMessage = "Error during message processing";

        when(redisTemplate.getStringSerializer()).thenReturn(StringRedisSerializer.UTF_8);
        when(message.getChannel()).thenReturn("invalidChannel".getBytes());
        when(message.getBody()).thenReturn("invalidBody".getBytes());

        doThrow(new RuntimeException(errorMessage)).when(objectMapper).readValue(anyString(), any(Class.class));

        // When
        assertThrows(RuntimeException.class, () -> redisSubscriber.onMessage(message, pattern));
    }
}