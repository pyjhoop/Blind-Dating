package com.blind.dating.service;

import com.blind.dating.domain.Chat;
import com.blind.dating.domain.ChatRoom;
import com.blind.dating.domain.ReadChat;
import com.blind.dating.dto.chat.ChatRequestDto;
import com.blind.dating.repository.ChatRepository;
import com.blind.dating.repository.ChattingRoomRepository;
import com.blind.dating.repository.ReadChatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class ChatService {

    private final ChatRepository chatRepository;
    private final ChattingRoomRepository chattingRoomRepository;
    private final ReadChatRepository readChatRepository;


    public Page<Chat> selectChatList(String roomId, Pageable pageable){
        return chatRepository.findAllByChatRoomId(Long.valueOf(roomId), pageable);
    }

    @Transactional
    public Chat saveChat(ChatRequestDto dto){
        // 속한 방번호를 통해 최근 메세지를 업데이트 후에 chat 저장하기
        ChatRoom room = chattingRoomRepository.findById(Long.valueOf(dto.getChatRoomId())).get();
        room.setRecentMessage(dto.getMessage());
        return chatRepository.save(dto.toEntity());
    }

    public Long unreadChat(Long userId, Long roomId){

        List<Chat> list = chatRepository.findAllByChatRoomIdOrderByIdDesc(roomId);
        Long listSize = Long.valueOf(list.size());

        Optional<ReadChat> readChat = readChatRepository.findByRoomIdAndUserId(roomId, userId);
        if(readChat.get().getChatId() == 0){
            return listSize;
        }else{

            return chatRepository.countByIdBetween(readChat.get().getChatId(), list.get(0).getId()) -1;
        }

    }
}
