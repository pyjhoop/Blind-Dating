package com.blind.dating.service;

import com.blind.dating.domain.ChatRoom;
import com.blind.dating.domain.UserAccount;
import com.blind.dating.dto.chat.ChatRoomDto;
import com.blind.dating.repository.ChattingRoomRepository;
import com.blind.dating.repository.querydsl.ChattingRoomRepositoryImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class ChattingRoomService {

    private final ChattingRoomRepository chatRoomRepository;
    private final ChattingRoomRepositoryImpl chattingRoomRepositoryImpl;
    private final ChatService chatService;

    public ChatRoom create(UserAccount user1, UserAccount user2){
       ChatRoom room = new ChatRoom(user1, user2);

        return chatRoomRepository.save(room);

    }

    /**
     * 현재 내가 참여하고 있는 채팅방 조회 서비스
     * @param userAccount
     * @return List<ChatRoomDto></ChatRoomDto>
     */
    public List<ChatRoomDto> getRooms( UserAccount userAccount){
        List<ChatRoomDto> rooms = chattingRoomRepositoryImpl.findAllByUserId(userAccount.getId()).stream().map(room -> {
            ChatRoomDto dto = new ChatRoomDto();

            // 조회를 위해 상대방 유저 정보 찾은 후 user2에 저장하기
            if(room.getUser1().getId() != userAccount.getId()){
                room.setUser2(room.getUser1());
            }
            // 채팅방에서 읽지 않은 채팅 개수 조회
            Long unReadCount = chatService.unreadChat(userAccount.getId(), room.getId());

            // 응답 데이터를 보여주기 위해 ChatRoom -> ChatRoomDto 로 변환
            dto.setRoomId(room.getId());
            dto.setUpdatedAt(room.getUpdatedAt());
            dto.setOtherUserid(room.getUser2().getId());
            dto.setOtherUserNickname(room.getUser2().getNickname());
            dto.setRecentMessage(room.getRecentMessage());
            dto.setUnReadCount(unReadCount);

            return dto;
        }).collect(Collectors.toList());

        return rooms;
    }

    public Optional<ChatRoom> getRoom(String roomId){

        return chatRoomRepository.findById(Long.valueOf(roomId));

    }

    public boolean removeRoom(ChatRoom chatRoom){

        if(chatRoom.getUser1() == null && chatRoom.getUser2() == null){
            chatRoomRepository.delete(chatRoom);
            return true;
        }
        return false;
    }

    @Transactional
    public ChatRoom leaveChatRoom(String roomId, UserAccount user){

        Optional<ChatRoom> chatRoom = chatRoomRepository.findById(Long.valueOf(roomId));

        if(chatRoom.isPresent()){
            ChatRoom room = chatRoom.get();

            if(room.getUser2().equals(user)){
                room.setUser2(null);
            }else if(room.getUser1().equals(user)){
                room.setUser1(null);
            }
            return room;
        }else {
            throw new RuntimeException(roomId +"에 해당하는 채팅방이 존재하지 않습니다.");
        }
    }
}
