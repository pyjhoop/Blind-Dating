package com.blind.dating.controller;

import com.blind.dating.config.SecurityConfig;
import com.blind.dating.domain.UserAccount;
import com.blind.dating.dto.UserAccountDto;
import com.blind.dating.security.TokenProvider;
import com.blind.dating.service.CustomUserDetailsService;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Disabled
@DisplayName("Auth 컨트롤러 - 테스트")
@Import(SecurityConfig.class)
@WebMvcTest(UserAccountController.class)
class UserAccountControllerTest {

    //TODO : 추후에 공부해서 추가하자!
    private final MockMvc mvc;

    @MockBean private UserAccountService userAccountService;
    @MockBean private TokenProvider tokenProvider;
    @MockBean private CustomUserDetailsService customUserDetailsService;

    UserAccountControllerTest(@Autowired MockMvc mvc) {
        this.mvc = mvc;
    }

    @DisplayName("[POST] 로그인 - 정상 호출")
    @Test
    public void givenUserIdAndPassword_whenLogin_thenReturnUserAccount() throws Exception {
        //Given
        UserAccountDto dto = UserAccountDto.of("user01","pass01","user1","서울",12,"INFP","M",false);
        ObjectMapper obj = new ObjectMapper();
        given(userAccountService.getByCredentials(dto.getUserId(),dto.getUserPassword())).willReturn(eq(dto.toEntity()));

        //When & Then
        mvc.perform(post("/api/login")
                .contentType(MediaType.APPLICATION_JSON)
                                .content(obj.writeValueAsString(dto))
                )
                .andExpect(status().isOk());
    }
}