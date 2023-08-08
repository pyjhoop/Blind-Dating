package com.blind.dating.controller;

import com.blind.dating.domain.UserAccount;
import com.blind.dating.dto.response.ResponseDto;
import com.blind.dating.dto.user.TokenDto;
import com.blind.dating.security.TokenProvider;
import com.blind.dating.service.TokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class TokenController {

    private final TokenService tokenService;
    private final TokenProvider tokenProvider;

    @GetMapping("/token/refresh")
    public ResponseEntity<ResponseDto<TokenDto>> newAccessToken(
            @RequestHeader("Authorization") String refreshTokenHeader,
            Authentication authentication
    ){
        String refreshToken = refreshTokenHeader.replace("Bearer ", "");

        UserAccount user = ((UserAccount)authentication.getPrincipal());

        // 토큰이 진짜인지 확인
        boolean check = tokenService.validRefreshToken(refreshToken,user);

        log.error(user.toString());


        if(check){
            String accessToken = tokenProvider.create(user);
            String newRefreshToken = tokenProvider.refreshToken(user);
            tokenService.updateRefreshToken(newRefreshToken,user);
            return ResponseEntity.ok().body(ResponseDto.<TokenDto>builder()
                    .status("OK").message("새로운 accessToken, refreshToken이 발급되었습니다.").data(
                            TokenDto.builder().accessToken(accessToken).refreshToken(newRefreshToken).build()
                    ).build());

        }else{
            return null;
        }
    }
}
