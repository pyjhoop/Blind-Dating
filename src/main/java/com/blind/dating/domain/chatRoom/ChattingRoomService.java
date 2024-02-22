package com.blind.dating.domain.chatRoom;

import com.blind.dating.common.code.ChattingRoomResponseCode;
import com.blind.dating.domain.chat.ChatService;
import com.blind.dating.domain.readChat.ReadChat;
import com.blind.dating.domain.user.UserAccount;
import com.blind.dating.dto.chat.ChatRoomDto;
import com.blind.dating.exception.ApiException;
import com.blind.dating.domain.user.UserAccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class ChattingRoomService {

    private final ChattingRoomRepository chatRoomRepository;
    private final ChatService chatService;
    private final UserAccountRepository userAccountRepository;
    private final ChattingRoomRepository chattingRoomRepository;
    private final ChatRoomUserAccountRepository chatRoomUserAccountRepository;


    @Transactional
    public List<ChatRoomDto> getRooms(Long userId){
         List<ChatRoomDto> rooms = chatRoomUserAccountRepository.findChatRoomsAndUsersByUserId(userId);

         // readChat의 chatId 부터 개수를 새는거야
        List<ChatRoomDto> newRooms = rooms.stream().map(chatRoomDto -> {

            Long count = chatService.unreadChat(userId, chatRoomDto.getRoomId());
            chatRoomDto.setUnReadCount(count-1);
            return chatRoomDto;
        }).toList();

        return newRooms;
    }

    public ChatRoomDto getRoom(Long userId, Long roomId){
        return chatRoomUserAccountRepository.findChatRoomByUserIdAndRoomId(userId, roomId)
                .orElseThrow(() -> new ApiException(ChattingRoomResponseCode.GET_ROOMS_FAIL));
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
