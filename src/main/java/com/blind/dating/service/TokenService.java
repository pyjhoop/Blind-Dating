package com.blind.dating.service;

import com.blind.dating.domain.UserAccount;
import com.blind.dating.dto.user.TokenDto;
import com.blind.dating.dto.user.UserIdRequestDto;
import com.blind.dating.dto.user.UserInfoWithTokens;
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


    /**
     * 리프레쉬 토큰 재발급 서비스
     * @param cookie
     * @return
     */
    public Boolean validRefreshToken(Cookie cookie){

        // 유효한지 확인해줘
        boolean validate = tokenProvider.validateToken(cookie.getValue());

        if(validate){
            return userAccountRepository.existsByRefreshToken(cookie.getValue());
        }else{
            return null;
        }
    }

    /**
     * 리프래쉬 토큰을 새로운 리프래쉬 토큰으로 업데이트
     * @param cookie
     * @return
     */
    @Transactional
    public UserInfoWithTokens updateRefreshToken(Cookie cookie){

        Optional<UserAccount> user = userAccountRepository.findByRefreshToken(cookie.getValue());
        String accessToken = null;
        String refreshToken = null;
        if(user.isPresent()){
            accessToken = tokenProvider.create(user.get());
            refreshToken = tokenProvider.refreshToken(user.get());
        }else{
            throw new RuntimeException(user.get().getId() +"에 해당하는 유저는 존재하지 앖습니다.");
        }

        return UserInfoWithTokens.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .id(user.get().getId())
                .nickname(user.get().getNickname())
                .build();
    }


}
