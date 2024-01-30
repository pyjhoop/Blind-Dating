package com.blind.dating.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@DisplayName("ChatRoom Entity - 테스트")
class ChatRoomTest {

    private ChatRoom chatRoom1;
    private ChatRoom chatRoom2;
    private Set<UserAccount> users = new LinkedHashSet<>();
    private Set<ReadChat> readChats = new LinkedHashSet<>();
    private Set<Chat> chats = new LinkedHashSet<>();

    @BeforeEach
    void setting() {
        users = Set.of(new UserAccount());
        readChats = Set.of(new ReadChat());
        chats = Set.of(new Chat(1L, new ChatRoom(), 1L, "message"));
        chatRoom1 = new ChatRoom(1L, users, readChats, true, "message");
        chatRoom2 = new ChatRoom(2L, users, readChats, true, "message");
    }

    @Test
    void equalsTest() {
        boolean result = chatRoom1.equals(new String());

        assertThat(result).isFalse();
    }

    @Test
    void getReadChats() {
        Set<ReadChat> readChats1 = chatRoom1.getReadChat();

        assertThat(readChats1).isEqualTo(readChats);
    }




}