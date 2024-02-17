package com.blind.dating.service;

import com.blind.dating.domain.chat.Chat;
import com.blind.dating.domain.chat.ChatService;
import com.blind.dating.domain.chatRoom.ChatRoom;
import com.blind.dating.dto.chat.ChatRequestDto;
import com.blind.dating.domain.chat.ChatRepository;
import com.blind.dating.domain.chatRoom.ChattingRoomRepository;
import com.blind.dating.domain.readChat.ReadChatRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@DisplayName("ChatService - 테스트")
@ExtendWith(MockitoExtension.class)
class ChatServiceTest {

    @Mock private ChatRepository chatRepository;
    @Mock private ChattingRoomRepository chattingRoomRepository;
    @Mock private ReadChatRepository readChatRepository;
    @InjectMocks private ChatService chatService;

    @DisplayName("챗 저장 - 테스트")
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

}