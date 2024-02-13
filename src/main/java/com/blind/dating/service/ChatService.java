package com.blind.dating.service;

import com.blind.dating.common.code.ChatResponseCode;
import com.blind.dating.domain.Chat;
import com.blind.dating.domain.ChatRoom;
import com.blind.dating.domain.ReadChat;
import com.blind.dating.dto.chat.ChatRequestDto;
import com.blind.dating.exception.ApiException;
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


    @Transactional(readOnly = true)
    public List<Chat> selectChatList(ChatRoom chatRoom, String chatId){
        if(Long.parseLong(chatId) == 0){
            return chatRepository.findAllByChatRoomOrderByIdDesc(chatRoom);
        }else{
            return chatRepository.findByChatRoomAndIdLessThanEqualOrderByIdDesc(chatRoom, Long.valueOf(chatId));
        }
    }

    @Transactional
    public Chat saveChat(ChatRequestDto dto){
        // 속한 방번호를 통해 최근 메세지를 업데이트 후에 chat 저장하기
        ChatRoom room = chattingRoomRepository.findById(Long.valueOf(dto.getChatRoomId()))
                        .orElseThrow(()-> new ApiException(ChatResponseCode.CHAT_NOT_FOUND));
        room.setRecentMessage(dto.getMessage());
        return chatRepository.save(Chat.of(room,Long.valueOf(dto.getWriterId()),dto.getMessage()));
    }

    @Transactional(readOnly = true)
    public Long unreadChat(Long userId, ChatRoom chatRoom){

        List<Chat> list = chatRepository.findAllByChatRoomOrderByIdDesc(chatRoom);
        Long listSize = (long) list.size();

        ReadChat readChat = readChatRepository.findByChatRoomAndUserId(chatRoom, userId)
                .orElseThrow(()-> new ApiException(ChatResponseCode.READ_CHAT_NOT_FOUND));

        if(readChat.getChatId() == 0){
            return listSize;
        }else{
            return chatRepository.countByIdBetween(readChat.getChatId(), list.get(0).getId()) -1;
        }

    }
}
