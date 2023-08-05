package com.blind.dating.service;

import com.blind.dating.domain.Message;
import com.blind.dating.domain.UserAccount;
import com.blind.dating.repository.MessageRepository;
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
import static org.mockito.BDDMockito.then;

@DisplayName("메세지 서비스 테스트")
@ExtendWith(MockitoExtension.class)
class MessageServiceTest {

    @Mock
    private MessageRepository messageRepository;

    @InjectMocks
    private MessageService messageService;

    @DisplayName("내게온 메세지 조회 테스트")
    @Test
    void givenReceiverId_whenSelectMessages_thenReturnMessages(){
        //Given
        List<Message> list = List.of();
        given(messageRepository.findAllByReceiver(anyLong())).willReturn(list);

        //When
        List<Message> result = messageService.getMessages(1L);

        //Then
        assertThat(result).isEmpty();
        then(messageRepository).should().findAllByReceiver(1L);
    }

    @DisplayName("내가 보낸 메세지 조회 테스트")
    @Test
    void givenUserAccount_whenSelectSentMessage_thenReturnMessages(){
        //Given
        List<Message> list = List.of();
        UserAccount user = UserAccount.of("userId","pwd","nick","서울",12,"INFP","M");
        user.setDeleted(false);
        given(messageRepository.findAllBySender(user)).willReturn(list);

        //When
        List<Message> result = messageService.getSentMessages(user);

        //Then
        assertThat(result).isEmpty();
        then(messageRepository).should().findAllBySender(user);

    }

    @DisplayName("메세지 단건 조회 테스트")
    @Test
    void givenMessageId_whenSelectMessage_thenReturnMessage(){
        //Given
        UserAccount user = UserAccount.of("userId","pwd","nick","서울",12,"INFP","M");
        user.setDeleted(false);
        Message message = Message.of(user,1L, "내용","UNREAD");

        given(messageRepository.findById(anyLong())).willReturn(Optional.of(message));

        //When
        Message result = messageService.getMessage(1L);

        //Then
        assertThat(result).isNotNull();
        then(messageRepository).should().findById(1L);


    }

    @DisplayName("메세지 생성 테스트")
    @Test
    void givenMessage_whenSaveMessage_thenReturnMessage(){
        //Given
        UserAccount user = UserAccount.of("userId","pwd","nick","서울",12,"INFP","M");
        user.setDeleted(false);
        Message message = Message.of(user,1L, "내용","UNREAD");
        given(messageRepository.save(any(Message.class))).willReturn(message);

        //When
        Message result = messageService.createMessage(message);

        //Then
        assertThat(result).isNotNull();
        then(messageRepository).should().save(message);

    }



}