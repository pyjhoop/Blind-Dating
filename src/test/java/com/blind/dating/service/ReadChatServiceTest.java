package com.blind.dating.service;

import com.blind.dating.domain.chatRoom.ChatRoom;
import com.blind.dating.domain.readChat.ReadChat;
import com.blind.dating.domain.chatRoom.ChattingRoomRepository;
import com.blind.dating.domain.readChat.ReadChatService;
import com.blind.dating.domain.readChat.ReadChatRepository;
import com.blind.dating.config.socket.WebSocketSessionManager;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@Disabled
@DisplayName("ReadChatService - 테스트")
@ExtendWith(MockitoExtension.class)
class ReadChatServiceTest {

    @Mock private ReadChatRepository readChatRepository;
    @Mock private WebSocketSessionManager websocketSessionManager;
    @Mock private ChattingRoomRepository chattingRoomRepository;
    @InjectMocks private ReadChatService readChatService;

    @DisplayName("읽은 채팅 업데이트 - 테스트")
    @Test
    void givenRoomIdANdChatId_whenUpdateReadChat_thenUpdate(){

        // Given
        Set<String> users = Set.of("1","2");
        ChatRoom chatRoom = new ChatRoom();
        Optional<ChatRoom> optional = Optional.of(chatRoom);
        ReadChat readChat1 = ReadChat.of(chatRoom, 1L, 1L);
        ReadChat readChat2 = ReadChat.of(chatRoom,2L, 1L);

//        given(websocketSessionManager.getUsers("1")).willReturn(users);
        given(chattingRoomRepository.findById(1L)).willReturn(optional);
        given(readChatRepository.findByChatRoomAndUserId(any(ChatRoom.class), anyLong())).willReturn(Optional.of(readChat1));

        //When
        readChatService.updateReadChat("1",1L);

        // Then
        verify(readChatRepository, times(2)).findByChatRoomAndUserId(any(ChatRoom.class), anyLong());
    }

    @DisplayName("읽은 채팅 업데이트시 예외 발생 - 테스트")
    @Test
    void givenRoomIdANdChatId_whenUpdateReadChat_thenThrowException() {
        Set<String> users = Set.of("1","2");

//        given(websocketSessionManager.getUsers("1")).willReturn(users);
        given(chattingRoomRepository.findById(anyLong())).willReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, ()-> {
            readChatService.updateReadChat("1", anyLong());
        });

        assertThat(exception.getMessage()).isEqualTo("메세지 전송시 예외가 발생했습니다.");
    }
}