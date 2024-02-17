package com.blind.dating.domain.readChat;

import com.blind.dating.domain.chatRoom.ChatRoom;
import com.blind.dating.domain.chatRoom.ChattingRoomRepository;
import com.blind.dating.domain.redis.SessionRedisRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@RequiredArgsConstructor
@Service
public class ReadChatService {
    private final ReadChatRepository readChatRepository;
    private final SessionRedisRepository sessionRedisRepository;
    private final ChattingRoomRepository chattingRoomRepository;

    @Transactional
    public void updateReadChat(String roomId, Long chatId){

        Set<String> users = sessionRedisRepository.getUsers(roomId);
        ChatRoom chatRoom = chattingRoomRepository.findById(Long.valueOf(roomId))
                .orElseThrow(()-> new RuntimeException("메세지 전송시 예외가 발생했습니다."));

        for(String u: users){
            // 있는지 확인부터 없으면 생성해주고 있으면 업데이트 해준다.
            ReadChat readChat = readChatRepository.findByChatRoomAndUserId(chatRoom, Long.valueOf(u))
                    .orElse(readChatRepository.save(ReadChat.of(chatRoom, Long.valueOf(u), chatId)));
            readChat.setChatId(chatId);
        }

    }
}
