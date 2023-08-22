package com.blind.dating.service;

import com.blind.dating.domain.ReadChat;
import com.blind.dating.dto.user.UserSession;
import com.blind.dating.handler.SessionHandler;
import com.blind.dating.repository.ReadChatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@RequiredArgsConstructor
@Service
public class ReadChatService {
    private final SessionHandler sessionHandler;
    private final ReadChatRepository readChatRepository;

    @Transactional
    public void updateReadChat(String roomId, Long chatId){

        ConcurrentHashMap<String, UserSession> users = sessionHandler.getUsers(roomId);

        ArrayList<UserSession> list = new ArrayList<>(users.values());

        for(UserSession u: list){
            // 있는지 확인부터 없으면 생성해주고 있으면 업데이트 해준다.
            Optional<ReadChat> readChat = readChatRepository.findByRoomIdAndUserId(Long.valueOf(roomId), Long.valueOf(u.getUserId()));
            if(readChat.isEmpty()){
                readChatRepository.save(ReadChat.of(Long.valueOf(roomId),Long.valueOf(u.getUserId()),chatId));
            }else{
                readChat.get().setChatId(chatId);
            }
        }

    }
}
