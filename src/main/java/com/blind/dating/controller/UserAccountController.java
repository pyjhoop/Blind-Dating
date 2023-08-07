package com.blind.dating.controller;

import com.blind.dating.domain.UserAccount;
import com.blind.dating.dto.user.LoginInputDto;
import com.blind.dating.dto.user.UserResponse;
import com.blind.dating.dto.response.ResponseDto;
import com.blind.dating.dto.user.UserRequestDto;
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
import org.springframework.web.bind.annotation.*;
 
@Tag(name = "UserAccount Info", description = "인증 관련 서비스")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class UserAccountController {

    private final UserAccountService userAccountService;
    private final TokenProvider tokenProvider;

    @Operation(summary = "회원가입", description = "유저정보를 받아서 회원가입을 진행합니다.")
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
    })
    @PostMapping("/signup")
    public ResponseDto<UserResponse> registerUser(
            @RequestBody UserRequestDto dto
            ){

        //TODO 필수데이터가 부족할때 통합 예외처리해주기
        UserAccount user = userAccountService.create(dto);
        String token = tokenProvider.create(user);

        return ResponseDto.<UserResponse>builder()
                .status("success")
                .message("회원가입이 성공적으로 완료되었습니다.")
                .data(UserResponse.from(user, token))
                .build();
    }

    @PostMapping("/login")
    @Operation(summary = "로그인", description = "아이디와 비밀번호를 받아서 로그인을 합니다.")
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
            @RequestBody LoginInputDto dto
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


    @PostMapping("/check-userId")
    @Operation(summary = "아이디 중복 체크", description = "아이디 중복을 체크합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST",content = @Content(schema = @Schema(implementation = ResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "NOT FOUND", content = @Content(schema = @Schema(implementation = ResponseDto.class))),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR", content = @Content(schema = @Schema(implementation = ResponseDto.class)))
    })
    @Parameter(name = "userId", description = "유저 아이디", example = "user01")
    public ResponseDto<Boolean> checkUserId(@RequestParam String userId){

        boolean check = userAccountService.checkUserId(userId);

        if(check){
            return ResponseDto.<Boolean>builder()
                    .status("OK")
                    .message("아이디가 존재합니다.")
                    .data(false).build();
        }else{
            return ResponseDto.<Boolean>builder()
                    .status("OK")
                    .message("아이디가 존재하지 않습니다.")
                    .data(true).build();
        }
    }

    @GetMapping("/check-nickname/{nickname}")
    @Operation(summary = "닉네임 중복 체크", description = "닉네임 중복 체크")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST",content = @Content(schema = @Schema(implementation = ResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "NOT FOUND", content = @Content(schema = @Schema(implementation = ResponseDto.class))),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR", content = @Content(schema = @Schema(implementation = ResponseDto.class)))
    })
    @Parameter(name = "nickname", description = "닉네임", example = "nick01")
    public ResponseDto<Boolean> checkNickname(@PathVariable String nickname){
        boolean check = userAccountService.checkNickname(nickname);

        if(check){
            return ResponseDto.<Boolean>builder()
                    .status("OK")
                    .message("닉네임이 존재합니다.")
                    .data(false).build();
        }else{
            return ResponseDto.<Boolean>builder()
                    .status("OK")
                    .message("닉네임이 존재하지 않습니다.")
                    .data(true).build();
        }

    }

}
