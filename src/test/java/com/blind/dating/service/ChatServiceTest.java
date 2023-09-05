package com.blind.dating.service;

import com.blind.dating.domain.Chat;
import com.blind.dating.domain.ChatRoom;
import com.blind.dating.domain.ReadChat;
import com.blind.dating.dto.chat.ChatRequestDto;
import com.blind.dating.repository.ChatRepository;
import com.blind.dating.repository.ChattingRoomRepository;
import com.blind.dating.repository.ReadChatRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;

@DisplayName("ChatService - 테스트")
@ExtendWith(MockitoExtension.class)
class ChatServiceTest {

    @Mock private ChatRepository chatRepository;
    @Mock private ChattingRoomRepository chattingRoomRepository;
    @Mock private ReadChatRepository readChatRepository;
    @InjectMocks private ChatService chatService;

    @DisplayName("챗 리스트 조회")
    @Test
    void givenRoomIdAndChatId_whenSelectChatList_thenReturnChatList(){
        Chat chat = new Chat();
        List<Chat> list = List.of(chat);

        given(chatRepository.findByChatRoomIdAndIdLessThanEqualOrderByIdDesc(1L,1L)).willReturn(list);

        List<Chat> result = chatService.selectChatList("1","1");

        assertThat(result).isNotNull();
    }

    @DisplayName("챗 저장")
    @Test
    void givenChatRequestDto_whenSaveChat_thenReturnChat(){
        ChatRequestDto dto = new ChatRequestDto("1","1","hi");
        ChatRoom chatRoom = new ChatRoom();
        Chat chat = new Chat(chatRoom, 1L,"hi");
        Optional<ChatRoom> optional = Optional.of(chatRoom);
        given(chattingRoomRepository.findById(1L)).willReturn(optional);
        given(chatRepository.save(any(Chat.class))).willReturn(chat);

        Chat result = chatService.saveChat(dto);

        assertThat(result).isNotNull();
    }

    @DisplayName("안읽은 채팅 개수 조회하기")
    @Test
    void givenUserIdAndRoomId_whenUpdateUnReadChat_thenReturnSize(){
        //Given
        Chat chat = new Chat();
        List<Chat> list = List.of(chat);
        Long listSize = (long) list.size();
        ReadChat readChat = ReadChat.of(1L,1L,0L);
        Optional<ReadChat> optional = Optional.of(readChat);

        given(readChatRepository.findByRoomIdAndUserId(anyLong(),anyLong())).willReturn(optional);

        //When
        Long result = chatService.unreadChat(1L,1L);

        //Then
        assertThat(result).isNotNull();
    }

}