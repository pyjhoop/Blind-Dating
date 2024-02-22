package com.blind.dating.domain.readChat;

import com.blind.dating.common.code.ChatResponseCode;
import com.blind.dating.config.socket.SessionManager;
import com.blind.dating.domain.chat.Chat;
import com.blind.dating.domain.chat.ChatRepository;
import com.blind.dating.domain.chatRoom.ChattingRoomRepository;
import com.blind.dating.exception.ApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.ConcurrentHashMap;

@RequiredArgsConstructor
@Service
public class ReadChatService {
    private final ReadChatRepository readChatRepository;
    private final ChattingRoomRepository chattingRoomRepository;
    private final ChatRepository chatRepository;

    @Transactional
    public void updateReadChat(Long roomId, Long userId){
        // chat을 가져와 최근꺼 1나만
        // readchet 을 수정해

        Chat chat = chatRepository.findFirstByChatRoomIdOrderByCreatedAtDesc(roomId)
                .orElseThrow(()-> new ApiException(ChatResponseCode.CHAT_NOT_FOUND));

        ReadChat readChat = readChatRepository.findByChatRoomIdAndUserId(roomId, userId)
                .orElseThrow(()-> new ApiException(ChatResponseCode.CHAT_NOT_FOUND));

        readChat.setChatId(chat.getId());
    }
}
