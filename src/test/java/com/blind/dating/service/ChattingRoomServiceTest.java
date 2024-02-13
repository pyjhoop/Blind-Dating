package com.blind.dating.service;

import com.blind.dating.common.code.ChattingRoomResponseCode;
import com.blind.dating.domain.ChatRoom;
import com.blind.dating.domain.UserAccount;
import com.blind.dating.dto.chat.ChatRoomDto;
import com.blind.dating.exception.ApiException;
import com.blind.dating.repository.ChattingRoomRepository;
import com.blind.dating.repository.UserAccountRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;

@DisplayName("ChattingRoomService - 테스트")
@ExtendWith(MockitoExtension.class)
class ChattingRoomServiceTest {

    @Mock private ChattingRoomRepository chattingRoomRepository;
    @Mock private ChatService chatService;
    @Mock private UserAccountRepository userAccountRepository;
    @InjectMocks private ChattingRoomService chattingRoomService;

    @DisplayName("채팅방 생성")
    @Test
    void givenUsers_whenCreateChatRoom_thenReturnChatRoom(){
        //Given
        UserAccount user1 = UserAccount.of("qweeqw","asdfdf", "nickname","asdf","asdf","M","하이요");
        UserAccount user2 = UserAccount.of("qweeqw","asdfdf", "nickname","asdf","asdf","M","하이요");
        Long userId1 = 1L;
        Long userId2 = 2L;

        ChatRoom room = new ChatRoom();

        given(chattingRoomRepository.save(room)).willReturn(room);

        //When
        ChatRoom result = chattingRoomService.create(user1, user2);

        //Then
        assertThat(result).isNotNull();
    }

    @DisplayName("채팅방 리스트 조회")
    @Test
    void givenUserId_whenSelectChatRooms_thenResultChatRoomDtoList(){
        //Given
        UserAccount user1 = new UserAccount(1L, "user01", "pass01", "nick01","서울","intp","M", false, "안녕", LocalDateTime.now(), null, "kakao",null,null,null,null);
        UserAccount user2 = new UserAccount(2L, "user02", "pass02", "nick02","서울","intp","W", false, "안녕", LocalDateTime.now(), null, "kakao",null,null,null,null);
        ChatRoom room = new ChatRoom();
        room.setUsers(Set.of(user1, user2));
        List<ChatRoom> list = List.of(room);

        given(userAccountRepository.findById(anyLong())).willReturn(Optional.of(user1));
        given(chattingRoomRepository.findAllByUsersAndStatusOrderByUpdatedAtDesc(user1, true)).willReturn(list);
        given(chatService.unreadChat(1L,room)).willReturn(1L);
        //When
        List<ChatRoomDto> result = chattingRoomService.getRooms(1L);

        //Then
        assertThat(result).isNotNull();
    }

    @DisplayName("채팅방 리스트 조회시 유저정보 불일치로 예외 발생")
    @Test
    void givenUserId_whenSelectChatRooms_thenThrowException() {
        // Given
        given(userAccountRepository.findById(anyLong())).willReturn(Optional.empty());

        // When
        ApiException exception = assertThrows(ApiException.class, ()->{
            chattingRoomService.getRooms(anyLong());
        });

        // Then
        assertThat(exception.getResponseCode()).isEqualTo(ChattingRoomResponseCode.GET_ROOMS_FAIL);

    }


    @DisplayName("채팅방 조회")
    @Test
    void givenRoomId_whenSelectChatRoom_thenReturnChatRoom(){
        //Given
        String roomId = "1";
        Long userId1 = 1L;
        Long userId2 = 2L;
        ChatRoom room = new ChatRoom();
        Optional<ChatRoom> optional = Optional.of(room);

        given(chattingRoomRepository.findById(Long.valueOf(roomId))).willReturn(optional);

        //When
        Optional<ChatRoom> result = chattingRoomService.getRoom(roomId);

        //Then
        assertThat(result).isNotNull();
    }

    @DisplayName("채팅방 나가기 true")
    @Test
    void givenRoomIdAndUserId_whenLeaveChatRoom_thenReturnTrue(){
        //Given
        Long userId1 = 1L;
        Long userId2 = 2L;
        ChatRoom room = new ChatRoom();
        room.setStatus(true);
        Optional<ChatRoom> optional = Optional.of(room);

        given(chattingRoomRepository.findById(1L)).willReturn(optional);

        //When
        Boolean result = chattingRoomService.leaveChatRoom("1","1");

        //Then
        assertThat(result).isFalse();
    }

    @DisplayName("채팅방 나가기 false")
    @Test
    void givenRoomIdAndUserId_whenLeaveChatRoom_thenReturnFalse(){
        //Given
        Long userId1 = 1L;
        Long userId2 = 2L;
        ChatRoom room = new ChatRoom();
        room.setStatus(false);
        Optional<ChatRoom> optional = Optional.of(room);

        given(chattingRoomRepository.findById(1L)).willReturn(optional);

        //When
        Boolean result = chattingRoomService.leaveChatRoom("1","1");

        //Then
        assertThat(result).isTrue();
    }

    @DisplayName("채팅방 나가기 예외 발생")
    @Test
    void givenRoomIdAndUserId_whenLeaveChatRoom_thenThrowException(){
        // Given
        given(chattingRoomRepository.findById(1L)).willReturn(Optional.empty());

        // When
        ApiException exception = assertThrows(ApiException.class, ()-> {
            chattingRoomService.leaveChatRoom("1", "1");
        });

        // Then
        assertThat(exception.getResponseCode()).isEqualTo(ChattingRoomResponseCode.GET_ROOMS_FAIL);
    }



}