package com.blind.dating.domain.chat;

import com.blind.dating.common.code.ChatResponseCode;
import com.blind.dating.common.code.ChattingRoomResponseCode;
import com.blind.dating.config.socket.SessionManager;
import com.blind.dating.domain.chatRoom.ChatRoom;
import com.blind.dating.domain.readChat.ReadChat;
import com.blind.dating.dto.chat.ChatRequestDto;
import com.blind.dating.dto.chat.ChatRoomDto;
import com.blind.dating.exception.ApiException;
import com.blind.dating.domain.chatRoom.ChattingRoomRepository;
import com.blind.dating.domain.readChat.ReadChatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.ConcurrentHashMap;

@RequiredArgsConstructor
@Service
public class ChatService {

    private final ChatRepository chatRepository;
    private final ChattingRoomRepository chattingRoomRepository;
    private final ReadChatRepository readChatRepository;
    private final SessionManager sessionManager;


    @Transactional(readOnly = true)
    public Page<Chat> selectChatList(ChatRoomDto chatRoomDto, Pageable pageable){
        ChatRoom chatRoom = chattingRoomRepository.findById(chatRoomDto.getRoomId())
                .orElseThrow(()-> new ApiException(ChattingRoomResponseCode.GET_ROOMS_FAIL));
        return chatRepository.findAllByChatRoom(chatRoom, pageable);
    }

    @Transactional
    public Chat saveChat(ChatRequestDto dto){
        // 속한 방번호를 통해 최근 메세지를 업데이트 후에 chat 저장하기
        ChatRoom room = chattingRoomRepository.findById(Long.valueOf(dto.getChatRoomId()))
                        .orElseThrow(()-> new ApiException(ChatResponseCode.CHAT_NOT_FOUND));
        room.setRecentMessage(dto.getMessage());

        // readChat도 여기서 설정해줘야해. -> 채팅방이 생기면 readChat도 생겨야해.
        // 첫번째 채팅으로 Chat도 생겨야함.
        Chat newChat = chatRepository.save(Chat.of(room,Long.valueOf(dto.getWriterId()),dto.getMessage()));

        ConcurrentHashMap<String, String> users = sessionManager.getUsers(String.valueOf(dto.getChatRoomId()));

        if(users.size() < 2) {
            ReadChat writerReadChat = readChatRepository.findByChatRoomAndUserId(room, Long.valueOf(dto.getWriterId()))
                    .orElseThrow(()-> new ApiException(ChatResponseCode.CHAT_SEND_FAIL));
            writerReadChat.setChatId(newChat.getId());
        }else {
            ReadChat writerReadChat = readChatRepository.findByChatRoomAndUserId(room, Long.valueOf(dto.getWriterId()))
                    .orElseThrow(()-> new ApiException(ChatResponseCode.CHAT_SEND_FAIL));
            writerReadChat.setChatId(newChat.getId());

            ReadChat recieverReadChat = readChatRepository.findByChatRoomAndUserId(room, Long.valueOf(dto.getReceiverId()))
                    .orElseThrow(()-> new ApiException(ChatResponseCode.CHAT_SEND_FAIL));
            recieverReadChat.setChatId(newChat.getId());
        }

        return newChat;
    }

    @Transactional(readOnly = true)
    public Long unreadChat(Long userId, Long chatRoomId){
        ReadChat readChat = readChatRepository.findByChatRoomIdAndUserId(chatRoomId, userId)
                .orElseThrow(()-> new ApiException(ChatResponseCode.CHAT_SEND_FAIL));

        // readChat의 chatId 부터 개수를 새는거야
        return chatRepository.countAllByChatRoomIdAndIdGreaterThanEqual(chatRoomId, readChat.getChatId());
    }
}
