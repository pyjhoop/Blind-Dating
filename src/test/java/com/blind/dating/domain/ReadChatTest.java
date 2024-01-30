package com.blind.dating.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ReadChat Entity - 테스트")
class ReadChatTest {

    private ReadChat readChat;

    @BeforeEach
    void setting() {
        readChat = ReadChat.of(new ChatRoom(1L,null,null, false, "message"), 1L,1L);
        readChat.setId(1L);
    }

    @Test
    void getUserIdTest() {
        Long userId = readChat.getUserId();
        assertThat(userId).isEqualTo(1L);
    }

    @Test
    void setUserIdTest() {
        readChat.setUserId(2L);
        Long userId = readChat.getUserId();
        assertThat(userId).isEqualTo(2L);
    }

    @Test
    void getIdTest() {
        Long id = readChat.getId();
        assertThat(id).isEqualTo(1L);
    }

    @Test
    void setIdTest() {
        readChat.setId(2L);
        Long id = readChat.getId();
        assertThat(id).isEqualTo(2L);
    }

    @Test
    void getChatRoomTest() {
        ChatRoom room = readChat.getChatRoom();
        ChatRoom compare = new ChatRoom(1L,null,null, false, "message");
        assertThat(room).isEqualTo(compare);
    }

    @Test
    void setChatRoomTest() {
        ChatRoom room = new ChatRoom(2L,null,null, false, "message");
        readChat.setChatRoom(room);
        ChatRoom result = readChat.getChatRoom();
        assertThat(result).isEqualTo(room);
    }

    @Test
    void toStringTest() {
        String str = readChat.toString();

        assertThat(str).isNotNull();
    }

}