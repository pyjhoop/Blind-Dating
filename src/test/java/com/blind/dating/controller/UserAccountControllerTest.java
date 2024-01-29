package com.blind.dating.controller;

import com.blind.dating.config.SecurityConfig;
import com.blind.dating.domain.UserAccount;
import com.blind.dating.dto.user.*;
import com.blind.dating.security.CustomAuthenticationEntryPoint;
import com.blind.dating.security.JwtAuthenticationFilter;
import com.blind.dating.security.TokenProvider;
import com.blind.dating.service.CustomUserDetailsService;
import com.blind.dating.service.UserAccountService;
import com.blind.dating.util.CookieUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;

import javax.servlet.http.Cookie;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@DisplayName("UserAccountController - 테스트")
@Import(SecurityConfig.class)
@WebMvcTest(UserAccountController.class)
@AutoConfigureMockMvc(addFilters = false)
class UserAccountControllerTest {

    @Autowired
    private MockMvc mvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Spy
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @MockBean private UserAccountService userAccountService;
    @MockBean private TokenProvider tokenProvider;
    @MockBean private JwtAuthenticationFilter jwtAuthenticationFilter;
    @MockBean private CustomUserDetailsService customUserDetailsService;
    @MockBean private CookieUtil cookieUtil;


    private UserRequestDto dto;
    private UserAccount user;
    @BeforeEach
    void setting(){
        dto = UserRequestDto.of("user01","userPass01","userNickname","서울","INFP","M","안녕하세요");
        dto.setInterests(List.of("자전거타기","놀기","게임하기"));
        dto.setQuestions(List.of(true, false, true));
        user = dto.toEntity();
        user.setRecentLogin(LocalDateTime.now());
        user.setDeleted(false);
        user.setUserPassword(bCryptPasswordEncoder.encode(dto.getUserPassword()));
    }

    @DisplayName("회원가입 테스트")
    @Test
    void givenUserRequest_whenRegister_thenSuccess() throws Exception {
       UserInfoWithTokens info = UserInfoWithTokens.builder()
               .id(1L)
               .accessToken("accessToken")
               .refreshToken("refreshToken")
               .nickname("userNickname")
               .build();

        UserRequestDto dto = UserRequestDto.of("userId","userPass01","userNickname","서울","INFP","M","안녕하세요");
        dto.setInterests(List.of("자전거타기","놀기","게임하기"));
        dto.setQuestions(List.of(true, false, true));

       given(userAccountService.register(any(UserRequestDto.class))).willReturn(dto.toEntity());


       mvc.perform(post("/api/signup")
               .contentType(MediaType.APPLICATION_JSON)
               .content(objectMapper.writeValueAsString(dto)))
               .andExpect(status().isOk());

    }
    @DisplayName("회원가입 테스트")
    @Test
    void givenUserRequest_whenRegister_thenErrors() throws Exception {
        // Given
        dto.setUserId("use");
        BindingResult bindingResult = new BeanPropertyBindingResult(dto, "userRequestDto");
        bindingResult.addError(new FieldError("userRequestDto", "password", "Password is too weak"));

        // When
        mvc.perform(post("/api/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());

    }


    @DisplayName("로그인 기능 테스트 - 로그인 성공")
    @Test
    void testLogin_WithSuccess() throws Exception {
        String userId = "userId";
        String userPassword = "userPwd";
        LoginInputDto dto = LoginInputDto.builder().userId(userId).userPassword(userPassword).build();

        LogInResponse response = LogInResponse.from(user, "accessToken", "refreshToken");
        UserInfoWithTokens userInfo = UserInfoWithTokens.builder()
                .accessToken("accessToken")
                .refreshToken("refreshToken")
                .nickname("nick")
                .build();

        given(userAccountService.getLoginInfo(userId, userPassword)).willReturn(response);

        mvc.perform(post("/api/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }


    @DisplayName("유저아이디 중복 체크 - 성공")
    @Test
    void testCheckUserId_WithSuccess() throws Exception {
        String userId = "userId";
        UserIdRequestDto dto = new UserIdRequestDto();
        dto.setUserId(userId);

        given(userAccountService.checkUserId(userId)).willReturn(false);

        mvc.perform(post("/api/check-userId")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }

    @DisplayName("유저아이디 중복 체크 - 실패")
    @Test
    void testCheckUserId_WithFail() throws Exception {
        String userId = "userId";
        UserIdRequestDto dto = new UserIdRequestDto();
        dto.setUserId(userId);

        given(userAccountService.checkUserId(userId)).willReturn(true);

        mvc.perform(post("/api/check-userId")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }

    @DisplayName("유저 닉네임 중복 체크 - 성공")
    @Test
    void givenUserNickname_whenCheckNickname_thenSuccess() throws Exception {
        String nickname = "nick";
        UserNicknameRequestDto dto = new UserNicknameRequestDto();
        dto.setNickname(nickname);

        given(userAccountService.checkNickname(nickname)).willReturn(false);

        mvc.perform(post("/api/check-nickname")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }

    @DisplayName("유저 닉네임 중복 체크 - 실패")
    @Test
    void givenUserNickname_whenCheckNickname_thenFail() throws Exception {
        String nickname = "nick";
        UserNicknameRequestDto dto = new UserNicknameRequestDto();
        dto.setNickname(nickname);

        given(userAccountService.checkNickname(nickname)).willReturn(true);

        mvc.perform(post("/api/check-nickname")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }

    @DisplayName("로그아웃 - 테스트")
    @Test
    void givenCookie_whenLogout_thenSuccess() throws Exception {
        Cookie cookie = new Cookie("refreshToken", "refreshToken");

        mvc.perform(post("/api/logout")
                .contentType(MediaType.APPLICATION_JSON)
                .cookie(cookie))
                .andExpect(status().isOk());
    }

}