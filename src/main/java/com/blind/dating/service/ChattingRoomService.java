package com.blind.dating.service;

import com.blind.dating.domain.ChatRoom;
import com.blind.dating.domain.UserAccount;
import com.blind.dating.dto.chat.ChatRoomDto;
import com.blind.dating.repository.ChattingRoomRepository;
import com.blind.dating.repository.querydsl.ChattingRoomRepositoryImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ChattingRoomService {

    private final ChattingRoomRepository chatRoomRepository;
    private final ChattingRoomRepositoryImpl chattingRoomRepositoryImpl;

    public ChatRoom create(UserAccount user1, UserAccount user2){
       ChatRoom room = new ChatRoom(user1, user2);

        return chatRoomRepository.save(room);

    }

    public Page<ChatRoom> getRooms(String userId, Pageable pageable){
        return chattingRoomRepositoryImpl.findAllByUserId(Long.valueOf(userId),Long.valueOf(userId), pageable);
    }

    public boolean checkRoomNumber(String roomId){

        return chatRoomRepository.existsById(Long.valueOf(roomId));

    }
}
