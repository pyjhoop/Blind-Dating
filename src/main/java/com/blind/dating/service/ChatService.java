package com.blind.dating.service;

import com.blind.dating.domain.Chat;
import com.blind.dating.dto.chat.ChatRequestDto;
import com.blind.dating.repository.ChatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
public class ChatService {

    private final ChatRepository chatRepository;


    public List<Chat> selectChatList(String roomId){
        return chatRepository.findAllByChatRoomIdOrderByIdAsc(Long.valueOf(roomId));
    }

    @Transactional
    public Chat saveChat(ChatRequestDto dto){
        return chatRepository.save(dto.toEntity());
    }
}
