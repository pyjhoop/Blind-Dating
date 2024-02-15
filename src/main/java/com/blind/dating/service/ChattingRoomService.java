package com.blind.dating.service;

import com.blind.dating.common.code.ChattingRoomResponseCode;
import com.blind.dating.domain.ChatRoom;
import com.blind.dating.domain.UserAccount;
import com.blind.dating.domain.UserChatRoom;
import com.blind.dating.dto.chat.ChatRoomDto;
import com.blind.dating.exception.ApiException;
import com.blind.dating.repository.ChattingRoomRepository;
import com.blind.dating.repository.UserAccountRepository;
import com.blind.dating.repository.UserChatRoomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class ChattingRoomService {

    private final ChattingRoomRepository chatRoomRepository;
    private final ChatService chatService;
    private final UserAccountRepository userAccountRepository;
    private final UserChatRoomRepository userChatRoomRepository;


    @Transactional
    public List<ChatRoomDto> getRooms(Long userId){

        UserAccount user = userAccountRepository.findById(userId)
                .orElseThrow(()-> new ApiException(ChattingRoomResponseCode.GET_ROOMS_FAIL));

        List<UserChatRoom> chatRooms = userChatRoomRepository.findAllByUserAccount(user);

        return chatRooms.stream().map(userRoom -> {
            return ChatRoomDto.From(userRoom.getChatRoom().getReceiver(), userRoom.getChatRoom(), 0L);
        }).toList();
    }

    public Optional<ChatRoom> getRoom(String roomId){

        return chatRoomRepository.findById(Long.valueOf(roomId));

    }


    @Transactional
    public Boolean leaveChatRoom(String roomId, String userId){

        ChatRoom chatRoom = chatRoomRepository.findById(Long.valueOf(roomId))
                .orElseThrow(()-> new ApiException(ChattingRoomResponseCode.GET_ROOMS_FAIL));

        if(!chatRoom.getStatus()){
            chatRoomRepository.delete(chatRoom);
            return true;

        }else {
            chatRoom.setStatus(false);
            return false;
        }

    }
}
