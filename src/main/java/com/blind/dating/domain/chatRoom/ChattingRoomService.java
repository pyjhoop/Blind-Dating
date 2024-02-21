package com.blind.dating.domain.chatRoom;

import com.blind.dating.common.code.ChattingRoomResponseCode;
import com.blind.dating.domain.chat.ChatService;
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
        return chatRoomUserAccountRepository.findChatRoomsAndUsersByUserId(userId);

//        UserAccount user = userAccountRepository.findById(userId)
//                .orElseThrow(()-> new ApiException(ChattingRoomResponseCode.GET_ROOMS_FAIL));
//
//        // userId로 chatRoom을 조회해야지
//
//        List<ChatRoom> chatRooms = chattingRoomRepository.findChatRoomsByUserIdOrderByUpdatedDateDesc(userId);
//
//        return chatRooms.stream().map(chatRoom -> {
//            UserAccount other = user;
//
////            for(UserAccount u : chatRoom.getUserAccounts()) {
////                if(user != u){
////                    other = u;
////                }
////            }
//
//            return ChatRoomDto.From(other, chatRoom, 0L);
//        }).toList();
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
