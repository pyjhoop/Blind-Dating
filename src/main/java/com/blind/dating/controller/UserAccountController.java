package com.blind.dating.controller;

import com.blind.dating.domain.UserAccount;
import com.blind.dating.dto.UserAccountDto;
import com.blind.dating.dto.UserWithMessageDto;
import com.blind.dating.dto.response.ResponseDto;
import com.blind.dating.security.TokenProvider;
import com.blind.dating.service.UserAccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class UserAccountController {

    private final UserAccountService userAccountService;
    private final TokenProvider tokenProvider;

    @PostMapping("/signup")
    public ResponseDto<UserWithMessageDto> registerUser(
            @RequestBody UserAccountDto dto
    ){
        //TODO 필수데이터가 부족할때 통합 예외처리해주기
        UserAccount user = userAccountService.create(dto);
        ResponseDto<UserAccountDto> response = new ResponseDto<>();
        String token = tokenProvider.create(user);
        return ResponseDto.<UserWithMessageDto>builder()
                .status("success")
                .message("회원가입이 성공적으로 완료되었습니다.")
                .data(UserWithMessageDto.from(user, token))
                .build();

    }

    @PostMapping("/login")
    public ResponseDto<UserWithMessageDto> authenticate(
            @RequestBody UserAccountDto dto
    ){
        UserAccount user = userAccountService.getByCredentials(dto.getUserId(), dto.getUserPassword());

        if(user == null){
            return ResponseDto.<UserWithMessageDto>builder()
                    .status("BAD REQUEST")
                    .message("로그인이 실패했습니다.")
                    .data(null)
                    .build();

        }else{
            String token = tokenProvider.create(user);
            return ResponseDto.<UserWithMessageDto>builder()
                    .status("OK")
                    .message("로그인이 성공적으로 처리되었습니다.")
                    .data(UserWithMessageDto.from(user, token))
                    .build();
        }
    }
}
