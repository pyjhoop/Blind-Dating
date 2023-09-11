package com.blind.dating.service;

import com.blind.dating.domain.UserAccount;
import com.blind.dating.dto.user.LogInResponse;
import com.blind.dating.dto.user.TokenDto;
import com.blind.dating.dto.user.UserIdRequestDto;
import com.blind.dating.dto.user.UserInfoWithTokens;
import com.blind.dating.repository.RefreshTokenRepository;
import com.blind.dating.repository.UserAccountRepository;
import com.blind.dating.security.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class TokenService {

    private final UserAccountRepository userAccountRepository;
    private final TokenProvider tokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;


    /**
     * 리프레쉬 토큰 재발급 서비스
     * @param cookie
     * @return
     */
    public String validRefreshToken(Cookie cookie){

        // 유효한지 확인해줘
        boolean validate = tokenProvider.validateToken(cookie.getValue());
        // id 추출해서 refreshToken 가져오기

        String userId = tokenProvider.validateAndGetUserId(cookie.getValue());


        if(validate){

            String refreshToken = refreshTokenRepository.getRefreshToken(userId);
            if(refreshToken != null){
                return userId;
            }else{
                return null;
            }
                    //userAccountRepository.existsByRefreshToken(cookie.getValue());
        }else{
            return null;
        }
    }

    /**
     * 리프래쉬 토큰을 새로운 리프래쉬 토큰으로 업데이트
     * @param userId
     * @return
     */
    @Transactional
    public LogInResponse updateRefreshToken(String userId){
        Optional<UserAccount> user = userAccountRepository.findById(Long.valueOf(userId));
        String accessToken = null;
        String refreshToken = null;
        if(user.isPresent()){
            accessToken = tokenProvider.create(user.get());
            refreshToken = tokenProvider.refreshToken(user.get());
            // redis에있는 이전 refreshToken 덮어쓰기
            refreshTokenRepository.save(userId, refreshToken);
        }else{
            throw new RuntimeException(user.get().getId() +"에 해당하는 유저는 존재하지 앖습니다.");
        }

        LogInResponse response = LogInResponse.from(user.get());
        response.setAccessToken(accessToken);
        response.setRefreshToken(refreshToken);

        return response;


    }


}
