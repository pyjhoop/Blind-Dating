package com.blind.dating.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Chat Entity - 테스트")
class ChatTest {
    private Chat chat;
    ChatRoom chatRoom;
    @BeforeEach
    void setting() {
        chatRoom = new ChatRoom();
        chat = new Chat(1L,chatRoom, 1L, "message");
    }

    @Test
    void testGetMessage() {
        // When
        String message = chat.getMessage();

        // Then
        assertEquals(message, "message");
    }

    @Test
    void testSetMessage() {
        // When
        String newMessage = "newMessage";
        chat.setMessage(newMessage);
        String result = chat.getMessage();

        // Then
        assertEquals(result, newMessage);
    }

    @Test
    void testGetWriterId() {
        // When;
        Long writerId = chat.getWriterId();

        // Then
        assertEquals(1L, writerId);
    }
    @Test
    void testSetWriterId() {
        // When;
        Long newWriterId = 2L;
        chat.setWriterId(newWriterId);

        Long result = chat.getWriterId();

        // Then
        assertEquals(2L, result);
    }
    @Test
    void testGetChatRoom() {
        // When
        ChatRoom chatRoom1 = chat.getChatRoom();

        // Then
        assertEquals(chatRoom1, chatRoom);
    }

}