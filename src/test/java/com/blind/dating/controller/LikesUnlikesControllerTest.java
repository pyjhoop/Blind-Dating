package com.blind.dating.controller;

import com.blind.dating.config.SecurityConfig;
import com.blind.dating.dto.user.UserReceiverId;
import com.blind.dating.security.TokenProvider;
import com.blind.dating.service.LikesUnlikesService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("LikesUnlikesController - 테스트")
@Import(SecurityConfig.class)
@WebMvcTest(LikesUnlikesController.class)
@AutoConfigureMockMvc(addFilters = false)
class LikesUnlikesControllerTest{

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean private LikesUnlikesService likesUnlikesService;
    @MockBean private TokenProvider tokenProvider;

    @Test
    @DisplayName("좋아요 성공")
    @WithMockUser(username = "1")
    void givenReceiverId_WhenLikeReceiver_ThenReturn200() throws Exception{
        //Given
        UserReceiverId userReceiverId = new UserReceiverId();
        userReceiverId.setReceiverId("1");

        given(likesUnlikesService.likeUser(any(Authentication.class),anyString())).willReturn(true);

        mvc.perform(post("/api/like")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userReceiverId)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("싫어요 성공")
    @WithMockUser(username = "1")
    void givenReceiverId_whenUnlikeReceiver_thenReturn200() throws Exception {
        UserReceiverId userReceiverId = new UserReceiverId();
        userReceiverId.setReceiverId("1");

        doNothing().when(likesUnlikesService).unlikeUser(any(Authentication.class), anyString());

        mvc.perform(post("/api/unlike")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userReceiverId)))
                .andExpect(status().isOk());
    }


}