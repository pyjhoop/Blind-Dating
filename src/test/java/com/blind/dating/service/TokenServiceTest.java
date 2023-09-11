package com.blind.dating.service;

import com.blind.dating.domain.UserAccount;
import com.blind.dating.dto.user.LogInResponse;
import com.blind.dating.dto.user.UserInfoWithTokens;
import com.blind.dating.repository.RefreshTokenRepository;
import com.blind.dating.repository.UserAccountRepository;
import com.blind.dating.security.TokenProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.test.context.support.WithMockUser;

import javax.servlet.http.Cookie;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;

@DisplayName("토큰 서비스 - 테스트")
@ExtendWith(MockitoExtension.class)
class TokenServiceTest {

    @Mock private UserAccountRepository userAccountRepository;
    @Mock private TokenProvider tokenProvider;
    @Mock private RefreshTokenRepository refreshTokenRepository;
    @InjectMocks private TokenService tokenService;


    @DisplayName("리프래시 토큰 재발급 서비스 -테스트")
    @Test
    void givenCookie_whenValidateRefreshToken_thenReturnUserId(){
        //Given
        Cookie cookie = new Cookie("refreshToken","refreshToken");

        given(tokenProvider.validateToken(cookie.getValue())).willReturn(true);
        given(tokenProvider.validateAndGetUserId(cookie.getValue())).willReturn("1");
        given(refreshTokenRepository.getRefreshToken("1")).willReturn("refreshToken");

        //When
        String userId = tokenService.validRefreshToken(cookie);

        //Then
        assertThat(userId).isEqualTo("1");
    }

    @DisplayName("리프래시 토큰 업데이트 - 테스트")
    @Test
    void givenUserId_whenUpdateRefreshToken_thenReturnUserInfoWithTokens(){
        //Given
        UserAccount user = UserAccount.of("qweeqw","asdfdf", "nickname","asdf","asdf","M","하이요");
        Optional<UserAccount> opUser = Optional.of(user);

        UserInfoWithTokens userInfo = UserInfoWithTokens.builder()
                        .accessToken("access").refreshToken("refresh")
                        .id(1L).nickname("nick").build();
        given(userAccountRepository.findById(1L)).willReturn(opUser);
        given(tokenProvider.create(user)).willReturn("access");
        given(tokenProvider.refreshToken(user)).willReturn("refresh");

        //When
        LogInResponse result = tokenService.updateRefreshToken("1");

        //Then
        assertThat(result).isNotNull();
        assertThat(result).hasFieldOrPropertyWithValue("accessToken","access");
        assertThat(result).hasFieldOrPropertyWithValue("refreshToken","refresh");
        assertThat(result).hasFieldOrPropertyWithValue("nickname","nickname");
    }




}