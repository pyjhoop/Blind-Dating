package com.blind.dating.service;

import com.blind.dating.repository.ReadChatRepository;
import com.blind.dating.repository.SessionRedisRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.LinkedHashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;

@DisplayName("ReadChatService - 테스트")
@ExtendWith(MockitoExtension.class)
class ReadChatServiceTest {

    @Mock private ReadChatRepository readChatRepository;
    @Mock private SessionRedisRepository sessionRedisRepository;
    @InjectMocks private ReadChatService readChatService;

    @DisplayName("읽은 채팅 업데이트 - 테스트")
    @Test
    void givenRoomIdANdChatId_whenUpdateReadChat_thenUpdate(){

        Set<String> users = new LinkedHashSet<>();
        users.add("1");
        users.add("2");
        given(sessionRedisRepository.getUsers("1")).willReturn(users);

        //When
        readChatService.updateReadChat("1",1L);
    }
}