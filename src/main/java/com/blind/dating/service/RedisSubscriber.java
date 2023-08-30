package com.blind.dating.service;


import com.blind.dating.dto.chat.ChatDto;
import com.blind.dating.dto.chat.ChatListWithUserId;
import com.blind.dating.dto.chat.ChatRequestDto;
import com.blind.dating.dto.chat.ChatRoomDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;
import org.w3c.dom.stylesheets.LinkStyle;

import java.util.List;

@RequiredArgsConstructor
@Service
public class RedisSubscriber implements MessageListener {

    private final ObjectMapper objectMapper;
    private final RedisTemplate redisTemplate;
    private final SimpMessageSendingOperations messagingTemplate;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            String channel = (String) redisTemplate.getStringSerializer().deserialize(message.getChannel());

            if(channel.equals("rooms")){
                String roomsStr = (String) redisTemplate.getStringSerializer().deserialize(message.getBody());
                ChatListWithUserId rooms = objectMapper.readValue(roomsStr, ChatListWithUserId.class);
                messagingTemplate.convertAndSend("/sub/chatroom/"+1, rooms);

            }else{
                String publishMessage = (String) redisTemplate.getStringSerializer().deserialize(message.getBody());

                ChatDto chatMessage = objectMapper.readValue(publishMessage, ChatDto.class);
                //웹소켓 구독자에게 메세지 send
                System.out.println(chatMessage.getMessage());
                messagingTemplate.convertAndSend("/sub/chat/room/" + chatMessage.getChatRoomId(), chatMessage);

            }


        }catch (Exception e){

        }
    }
}
