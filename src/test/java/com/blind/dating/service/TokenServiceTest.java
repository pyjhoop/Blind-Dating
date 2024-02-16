package com.blind.dating.service;

import com.blind.dating.common.code.TokenResponseCode;
import com.blind.dating.domain.interest.Interest;
import com.blind.dating.domain.token.TokenService;
import com.blind.dating.domain.user.UserAccount;
import com.blind.dating.dto.user.LogInResponse;
import com.blind.dating.exception.ApiException;
import com.blind.dating.domain.token.RefreshTokenRepository;
import com.blind.dating.domain.user.UserAccountRepository;
import com.blind.dating.security.TokenProvider;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


import java.util.List;
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
        given(tokenProvider.validateAndGetUserId(cookie.getValue())).willReturn("1");
        given(refreshTokenRepository.getRefreshToken("1")).willReturn("refreshToken");

        //When
        String userId = tokenService.validRefreshToken(cookie);

        //Then
        assertThat(userId).isEqualTo("1");
    }

    @DisplayName("리프레시 토큰 예외 발생 - 테스트")
    @Test
    void givenCookie_whenValidateRefreshToken_thenThrowException() {
        Cookie cookie = new Cookie("refreshToken","refreshToken");
        given(tokenProvider.validateAndGetUserId(cookie.getValue())).willReturn("1");
        given(refreshTokenRepository.getRefreshToken("1")).willReturn(null);

        ApiException exception = assertThrows(ApiException.class, ()->{
            tokenService.validRefreshToken(cookie);
        });

        assertThat(exception.getResponseCode()).isEqualTo(TokenResponseCode.NOT_FOUND_REFRESH_TOKEN);
    }


    @DisplayName("리프레시 토큰 업데이트 - 테스트")
    @Test
    void givenUserId_whenUpdateRefreshToken_thenReturnUserInfoWithTokens(){
        //Given
        UserAccount user = UserAccount.of("qweeqw","asdfdf", "nickname","asdf","asdf","M","하이요");
        user.setInterests(List.of(new Interest()));
        user.setQuestions(List.of(new Question()));
        given(userAccountRepository.findById(1L)).willReturn(Optional.of(user));
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

    @DisplayName("리프레시 토큰 업데이트시 예외 발생 - 테스트")
    @Test
    void givenUserId_whenUpdateRefreshToken_thenThrowException() {
        // Given
        given(userAccountRepository.findById(1L)).willReturn(Optional.empty());

        // When
        ApiException exception = assertThrows(ApiException.class, () -> {
            tokenService.updateRefreshToken("1");
        });

        assertThat(exception.getResponseCode()).isEqualTo(TokenResponseCode.NOT_FOUND_REFRESH_TOKEN);
    }




}