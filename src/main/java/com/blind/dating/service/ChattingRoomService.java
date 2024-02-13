package com.blind.dating.service;

import com.blind.dating.common.code.ChattingRoomResponseCode;
import com.blind.dating.domain.ChatRoom;
import com.blind.dating.domain.UserAccount;
import com.blind.dating.dto.chat.ChatRoomDto;
import com.blind.dating.exception.ApiException;
import com.blind.dating.repository.ChattingRoomRepository;
import com.blind.dating.repository.UserAccountRepository;
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

//    public ChatRoom create(UserAccount user1, UserAccount user2){
//       ChatRoom room = new ChatRoom();
//       room.getUsers().add(user1);
//       room.getUsers().add(user2);
//
//        return chatRoomRepository.save(room);
//
//    }


//    @Transactional
//    public List<ChatRoomDto> getRooms(Long userId){
//
//        UserAccount user = userAccountRepository.findById(userId)
//                .orElseThrow(()-> new ApiException(ChattingRoomResponseCode.GET_ROOMS_FAIL));
//
//        List<ChatRoom> chatRooms = chatRoomRepository.findAllByUsersAndStatusOrderByUpdatedAtDesc(user, true);
//
//        return chatRooms.stream().map(room -> {
//            Set<UserAccount> users = room.getUsers();
//            UserAccount other = users.stream().filter(
//                    it -> !it.equals(user)
//            ).collect(Collectors.toList()).get(0);
//
//            // 채팅방에서 읽지 않은 채팅 개수 조회
//            Long unReadCount = chatService.unreadChat(userId, room);
//            // 상대방 정보 조회하기
//            return ChatRoomDto.From(other, room, unReadCount);
//        }).collect(Collectors.toList());
//    }

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
