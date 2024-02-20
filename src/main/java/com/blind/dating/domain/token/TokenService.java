package com.blind.dating.domain.token;

import com.blind.dating.common.code.TokenResponseCode;
import com.blind.dating.domain.user.UserAccount;
import com.blind.dating.domain.user.dto.LogInResponse;
import com.blind.dating.exception.ApiException;
import com.blind.dating.domain.user.UserAccountRepository;
import com.blind.dating.security.TokenProvider;
import jakarta.servlet.http.Cookie;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@RequiredArgsConstructor
@Service
public class TokenService {

    private final UserAccountRepository userAccountRepository;
    private final TokenProvider tokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;


    /**
     * 리프레쉬 토큰 재발급 서비스
     * @param cookie
     * @return String
     */
    public String validRefreshToken(Cookie cookie){
        return tokenProvider.validateAndGetUserId(cookie.getValue());
    }

    /**
     * 리프래쉬 토큰을 새로운 리프래쉬 토큰으로 업데이트
     * @param userId
     * @return LoginResponse.class
     */
    @Transactional
    public LogInResponse updateRefreshToken(String userId){
        UserAccount user = userAccountRepository.findById(Long.valueOf(userId))
                .orElseThrow(() -> new ApiException(TokenResponseCode.NOT_FOUND_REFRESH_TOKEN));

        String accessToken = tokenProvider.create(user);
        String refreshToken = tokenProvider.refreshToken(user);
        refreshTokenRepository.save(Long.valueOf(userId), refreshToken);

        return LogInResponse.from(user, accessToken, refreshToken);
    }


}
