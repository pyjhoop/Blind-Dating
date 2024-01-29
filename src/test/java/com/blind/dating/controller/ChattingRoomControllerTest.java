package com.blind.dating.controller;

import com.blind.dating.config.SecurityConfig;
import com.blind.dating.domain.ChatRoom;
import com.blind.dating.domain.UserAccount;
import com.blind.dating.dto.chat.ChatRoomDto;
import com.blind.dating.security.JwtAuthenticationFilter;
import com.blind.dating.service.ChatService;
import com.blind.dating.service.ChattingRoomService;
import com.blind.dating.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("ChattingRoomController - 테스트")
@WebMvcTest(ChattingRoomController.class)
@Import(SecurityConfig.class)
@AutoConfigureMockMvc(addFilters = false)
class ChattingRoomControllerTest {

    @Autowired private MockMvc mvc;
    @MockBean private ChattingRoomService chattingRoomService;
    @MockBean private JwtAuthenticationFilter jwtAuthenticationFilter;
    @MockBean private ChatService chatService;
    @MockBean private UserService userService;

    @DisplayName("채팅방 리스트 조회 - 테스트")
    @Test
    @WithMockUser(username = "1")
    void givenAuthentication_whenGetMessageList_thenReturnList() throws Exception{
        // Given
        Authentication authentication = new UsernamePasswordAuthenticationToken("1",null);
        List<ChatRoomDto> rooms = List.of(new ChatRoomDto(1L, LocalDateTime.now(), 1L, "nick1","하이요",3L));
        given(chattingRoomService.getRooms(anyLong())).willReturn(rooms);

        // When
        mvc.perform(get("/api/chatroom")
                .contentType(MediaType.APPLICATION_JSON)
                        .principal(authentication))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OK"));


    }

    @DisplayName("채팅방 메시지 리스트 조회시 채팅방 조회 실패예외 - 테스트")
    @Test
    @WithMockUser(username = "1")
    void givenRoomIdAndChatId_whenEnterRoom_thenThrowException() throws Exception{
        Authentication authentication = new UsernamePasswordAuthenticationToken("1",null);
        given(chattingRoomService.getRoom(anyString())).willReturn(Optional.empty());

        mvc.perform(get("/api/chatroom/1")
                .contentType(MediaType.APPLICATION_JSON)
                        .queryParam("chatId","1")
                .principal(authentication))
                .andExpect(status().isBadRequest());
    }

    @DisplayName("채팅방 메시지 리스트 조회 - 테스트")
    @Test
    @WithMockUser(username = "1")
    void givenRoomIdAndChatId_whenEnterRoom_thenReturnChatList() throws Exception {
        Authentication authentication = new UsernamePasswordAuthenticationToken("1",null);
        UserAccount user1 = new UserAccount(1L, "user01", "pass01", "nick01","서울","intp","M", false, "안녕", LocalDateTime.now(), null, "kakao");
        UserAccount user2 = new UserAccount(2L, "user02", "pass02", "nick02","서울","intp","W", false, "안녕", LocalDateTime.now(), null, "kakao");
        ChatRoom chatRoom = new ChatRoom();
        chatRoom.setUsers(Set.of(user1, user2));
        given(chattingRoomService.getRoom(anyString())).willReturn(Optional.of(chatRoom));
        given(chatService.selectChatList(chatRoom,"1")).willReturn(anyList());

        mvc.perform(get("/api/chatroom/1")
                .contentType(MediaType.APPLICATION_JSON)
                .principal(authentication)
                .queryParam("chatId","1"))
                .andExpect(status().isOk());
    }



}