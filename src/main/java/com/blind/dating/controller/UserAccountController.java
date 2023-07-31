package com.blind.dating.controller;

import com.blind.dating.domain.UserAccount;
import com.blind.dating.dto.LoginUserDto;
import com.blind.dating.dto.UserAccountDto;
import com.blind.dating.dto.response.UserResponse;
import com.blind.dating.dto.response.ResponseDto;
import com.blind.dating.security.TokenProvider;
import com.blind.dating.service.UserAccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "UserAccount Api", description = "인증 관련 API")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class UserAccountController {

    private final UserAccountService userAccountService;
    private final TokenProvider tokenProvider;

    @Operation(summary = "Register Post", description = "유저정보를 받아서 회원가입을 진행한다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST",content = @Content(schema = @Schema(implementation = ResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "NOT FOUND", content = @Content(schema = @Schema(implementation = ResponseDto.class))),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR", content = @Content(schema = @Schema(implementation = ResponseDto.class)))

    })
    @Parameters({
            @Parameter(name = "userId", description = "유저 아이디", example = "user01"),
            @Parameter(name = "userPassword", description = "비밀번호", example = "qwe123!"),
            @Parameter(name = "nickname", description = "닉네임", example = "사과"),
            @Parameter(name = "region", description = "사는 지역", example = "경기도, 강원도, 경상도..."),
            @Parameter(name = "score", description = "가중치 점수", example = "5"),
            @Parameter(name = "mbti", description = "MBTI", example = "INFP"),
            @Parameter(name = "gender", description = "성별", example = "M"),
            @Parameter(name = "deleted", description = "회원 탈퇴 여부", example = "false")
    })
    @PostMapping("/signup")
    public ResponseDto<UserResponse> registerUser(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "유저정보") @RequestBody UserAccountDto dto
    ){
        //TODO 필수데이터가 부족할때 통합 예외처리해주기
        UserAccount user = userAccountService.create(dto);
        ResponseDto<UserAccountDto> response = new ResponseDto<>();
        String token = tokenProvider.create(user);
        return ResponseDto.<UserResponse>builder()
                .status("success")
                .message("회원가입이 성공적으로 완료되었습니다.")
                .data(UserResponse.from(user, token))
                .build();
    }

    @PostMapping("/login")
    @Operation(summary = "Login POST", description = "아이디와 비밀번호를 받아서 로그인을 한다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST",content = @Content(schema = @Schema(implementation = ResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "NOT FOUND", content = @Content(schema = @Schema(implementation = ResponseDto.class))),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR", content = @Content(schema = @Schema(implementation = ResponseDto.class)))

    })
    @Parameters({
            @Parameter(name = "userId", description = "유저 아이디",example = "user01"),
            @Parameter(name = "userPassword", description = "유저 비밀번호", example = "pass01")
    })
    public ResponseDto<UserResponse> authenticate(
            @RequestBody LoginUserDto dto
    ){
        UserAccount user = userAccountService.getByCredentials(dto.getUserId(), dto.getUserPassword());

        if(user == null){
            return ResponseDto.<UserResponse>builder()
                    .status("BAD REQUEST")
                    .message("로그인이 실패했습니다.")
                    .data(null)
                    .build();

        }else{
            String token = tokenProvider.create(user);
            return ResponseDto.<UserResponse>builder()
                    .status("OK")
                    .message("로그인이 성공적으로 처리되었습니다.")
                    .data(UserResponse.from(user, token))
                    .build();
        }
    }
}
