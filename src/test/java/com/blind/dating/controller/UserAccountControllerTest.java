package com.blind.dating.controller;

import com.blind.dating.config.SecurityConfig;
import com.blind.dating.domain.UserAccount;
import com.blind.dating.dto.user.LoginInputDto;
import com.blind.dating.dto.user.UserAccountDto;
import com.blind.dating.dto.user.UserIdRequestDto;
import com.blind.dating.dto.user.UserRequestDto;
import com.blind.dating.security.TokenProvider;
import com.blind.dating.service.CustomUserDetailsService;
import com.blind.dating.service.InterestService;
import com.blind.dating.service.UserAccountService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("Auth 컨트롤러 - 테스트")
@Import(SecurityConfig.class)
@WebMvcTest(UserAccountController.class)
class UserAccountControllerTest {

    private final MockMvc mvc;

    @MockBean private UserAccountService userAccountService;
    @MockBean private TokenProvider tokenProvider;
    @MockBean private CustomUserDetailsService customUserDetailsService;
    @MockBean private InterestService interestService;

    UserAccountControllerTest(@Autowired MockMvc mvc) {
        this.mvc = mvc;
    }

    @DisplayName("[POST] 로그인 - 정상 호출")
    @Test
    public void givenUserIdAndPassword_whenLogin_thenReturnUserAccount() throws Exception {
        //Given
        UserAccount user = UserAccount.of("user01","pass01","user1","서울",12,"INFP","M","하이요");
        ObjectMapper obj = new ObjectMapper();
        given(userAccountService.getByCredentials(anyString(), anyString())).willReturn(user);
        String token = "token";
        given(tokenProvider.create(any(UserAccount.class))).willReturn(token);
        LoginInputDto dto1 = LoginInputDto.builder()
                        .userId("user01").userPassword("pass01").build();

        //When & Then
        mvc.perform(post("/api/login")
                .contentType(MediaType.APPLICATION_JSON)
                                .content(obj.writeValueAsString(dto1))
                ).andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.data.nickname").value("user1"));

    }

    @DisplayName("회원가입 테스트")
    @Test
    void givenUserAccountDto_whenSignUp_thenReturnUser() throws Exception {
        //Given
        UserRequestDto user = UserRequestDto.of("user01","pass01","user1","서울",12,"INFP","M","하이요");
        String token = "token";
        given(userAccountService.create(any(UserRequestDto.class), anyString())).willReturn(user.toEntity());
        given(tokenProvider.create(any(UserAccount.class))).willReturn(token);
        given(tokenProvider.refreshToken(any(UserAccount.class))).willReturn(token);

        ObjectMapper obj = new ObjectMapper();

        //When & Then
        mvc.perform(post("/api/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(obj.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.nickname").value("user1"));

    }

    @DisplayName("아이디 중복 체크 - 아이디 이미 존재할때")
    @Test
    void givenUserId_whenCheckUserId_thenReturnTrue() throws Exception {
        //Given
        String userId = "userId";
        given(userAccountService.checkUserId(userId)).willReturn(false);

        UserIdRequestDto userIdRequestDto = new UserIdRequestDto();
        userIdRequestDto.setUserId("userId");
        ObjectMapper obj = new ObjectMapper();


        //When & Then
        mvc.perform(post("/api/check-userId")
                .contentType(MediaType.APPLICATION_JSON)
                .content(obj.writeValueAsString(userIdRequestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.data").value(true));
    }

    @DisplayName("아이디 중복 체크 - 아이디 없을때")
    @Test
    void givenUserId_whenCheckUserId_thenReturnFalse() throws Exception {
        //Given
        String userId = "userId";
        given(userAccountService.checkUserId(userId)).willReturn(true);
        UserIdRequestDto userIdRequestDto = new UserIdRequestDto();
        userIdRequestDto.setUserId("userId");
        ObjectMapper obj = new ObjectMapper();

        //When & Then
        mvc.perform(post("/api/check-userId")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(obj.writeValueAsString(userIdRequestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.data").value(false));
    }

    @DisplayName("닉네임 중복 체크 - 닉네임 이미 존재할때")
    @Test
    void givenNickname_whenCheckNickname_thenReturnTrue() throws Exception {
        //Given
        String nickname = "user1";
        given(userAccountService.checkNickname(nickname)).willReturn(false);

        //When & Then
        mvc.perform(get("/api/check-nickname/"+nickname))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.data").value(true));
    }

    @DisplayName("닉네임 중복 체크 - 닉네임이 없을 때")
    @Test
    void givenNickname_whenCheckNickname_thenReturnFalse() throws Exception {
        //Given
        String nickname = "user1";
        given(userAccountService.checkNickname(nickname)).willReturn(true);

        //When & Then
        mvc.perform(get("/api/check-nickname/"+nickname))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.data").value(false));
    }

}