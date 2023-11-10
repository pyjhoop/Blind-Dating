package com.blind.dating.controller;

import com.blind.dating.domain.UserAccount;
import com.blind.dating.dto.user.UserIdWithNicknameAndGender;
import com.blind.dating.dto.user.UserUpdateRequestDto;
import com.blind.dating.repository.UserAccountRedisRepository;
import com.blind.dating.security.TokenProvider;
import com.blind.dating.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("UserController -테스트")
@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mvc;
    @Autowired
    private ObjectMapper obj;


    @MockBean private UserService userService;
    @MockBean private TokenProvider tokenProvider;
    @MockBean private UserAccountRedisRepository userAccountRedisRepository;

    private UserAccount user;
    private Authentication authentication;

    @BeforeEach
    void setUp(){
        user = UserAccount.of("qweeqw","asdfdf", "nickname","asdf","asdf","M","하이요");
        authentication = new UsernamePasswordAuthenticationToken("1",user.getPassword());
    }

    @DisplayName("추천 유저 조회")
    @Test
    @WithMockUser(username = "1")
    public void givenUserInfo_WhenSelectUserList_ThenReturnUserList() throws Exception {

        //Given
        Pageable pageable = Pageable.ofSize(10);
        List<UserAccount> list = List.of(user);
        Page<UserAccount> pages = new PageImpl<>(list, pageable, 1);

        given(userService.getUserList(any(Authentication.class), any(Pageable.class)))
                .willReturn(pages);

        mvc.perform(get("/api/user-list")
                        .header("Authorization", "Bearer "+"refreshToken")
                        .queryParam("page","0")
                        .queryParam("size","10")) // 첫 번째 페이지 요청
                .andExpect(status().isOk());

    }


    @DisplayName("내정보 조회")
    @Test
    @WithMockUser(username = "1")
    public void givenUserInfo_WhenSelectMyInfo_ThenReturnMyInfo() throws Exception {
        //Given
        given(userService.getMyInfo(any(Authentication.class))).willReturn(user);

        //When && Then
        mvc.perform(get("/api/user")
                        .header("Authorization", "Bearer "+"refreshToken"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));

        then(userService).should().getMyInfo(any(Authentication.class));
    }


    @DisplayName("내정보 수정")
    @Test
    @WithMockUser(username = "1")
    public void givenUserInfo_WhenUpdateMyInfo_ThenReturnMyInfo() throws Exception {
        List<String> list = new ArrayList<>();
        list.add("놀기");
        list.add("피하기");
        UserUpdateRequestDto dto = UserUpdateRequestDto.builder()
                .region("인천")
                .mbti("MBTI")
                .selfIntroduction("하이요")
                .interests(list).build();

        // Mock the updated user returned by the service.
        UserAccount updatedUser = UserAccount.of("h1","g1","g1","g","gd","gd","fd");


        given(userService.updateMyInfo(any(Authentication.class), eq(dto))).willReturn(user);
        given(userService.getMyInfo(any(Authentication.class))).willReturn(user);

        mvc.perform(put("/api/user")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer "+"refreshToken")
                .content(obj.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }

}