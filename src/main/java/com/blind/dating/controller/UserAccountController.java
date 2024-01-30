package com.blind.dating.controller;

import com.blind.dating.common.code.ResponseCode;
import com.blind.dating.dto.user.*;
import com.blind.dating.common.Api;
import com.blind.dating.service.UserAccountService;
import com.blind.dating.util.CookieUtil;
import com.blind.dating.common.code.UserResponseCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@Slf4j
@Tag(name = "UserAccount Info", description = "인증 관련 서비스")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class UserAccountController {

    private final UserAccountService userAccountService;

    @Operation(summary = "회원가입", description = "유저정보를 받아서 회원가입을 진행합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST",content = @Content(schema = @Schema(implementation = Api.class))),
            @ApiResponse(responseCode = "404", description = "NOT FOUND", content = @Content(schema = @Schema(implementation = Api.class))),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR", content = @Content(schema = @Schema(implementation = Api.class)))

    })
    @Parameters({
            @Parameter(name = "userId", description = "유저 아이디", example = "user01"),
            @Parameter(name = "userPassword", description = "비밀번호", example = "qwe123!"),
            @Parameter(name = "nickname", description = "닉네임", example = "사과"),
            @Parameter(name = "region", description = "사는 지역", example = "경기도, 강원도, 경상도..."),
            @Parameter(name = "mbti", description = "MBTI", example = "INFP"),
            @Parameter(name = "gender", description = "성별", example = "M"),
            @Parameter(name = "interests", description = "관심사", example = "['자전거','독서']"),
            @Parameter(name = "questions", description = "질문에 대한 답변", example = "[true,false]"),
            @Parameter(name = "selfIntroduction", description = "자기소개", example = "hello")
    })
    @PostMapping("/signup")
    public ResponseEntity<Api> registerUser(
            @RequestBody @Valid UserRequestDto dto,
            BindingResult bindingResult
            ) throws MethodArgumentNotValidException {
        if (bindingResult.hasErrors()) {
            throw new MethodArgumentNotValidException(null, bindingResult);
        }

        userAccountService.register(dto);
        return ResponseEntity.ok()
                .body(Api.OK(UserResponseCode.REGISTER_SUCCESS,null));
    }

    @PostMapping("/login")
    @Operation(summary = "로그인", description = "아이디와 비밀번호를 받아서 로그인을 합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST",content = @Content(schema = @Schema(implementation = Api.class))),
            @ApiResponse(responseCode = "404", description = "NOT FOUND", content = @Content(schema = @Schema(implementation = Api.class))),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR", content = @Content(schema = @Schema(implementation = Api.class)))

    })
    @Parameters({
            @Parameter(name = "userId", description = "유저 아이디",example = "user01"),
            @Parameter(name = "userPassword", description = "유저 비밀번호", example = "pass01")
    })
    public ResponseEntity<Api<?>> authenticate(
            @RequestBody LoginInputDto dto,
            HttpServletResponse response
            ){

        LogInResponse user = userAccountService.getLoginInfo(dto.getUserId(), dto.getUserPassword());
        LogInResponseDto userInfo = LogInResponseDto.from(user);
        userInfo.setAccessToken(user.getAccessToken());

        CookieUtil.addCookie(response, "refreshToken", user.getRefreshToken());

        return ResponseEntity.ok()
                .body(Api.OK(UserResponseCode.LOGIN_SUCCESS, userInfo));
    }

    @PostMapping("/logout")
    @Operation(summary = "로그아웃", description = "로그아웃을 진행합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST",content = @Content(schema = @Schema(implementation = Api.class))),
            @ApiResponse(responseCode = "404", description = "NOT FOUND", content = @Content(schema = @Schema(implementation = Api.class))),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR", content = @Content(schema = @Schema(implementation = Api.class)))

    })
    public ResponseEntity<Api> logOut(
            @CookieValue(name = "refreshToken") Cookie cookie,
            HttpServletResponse response){

        cookie.setMaxAge(0);
        response.addCookie(cookie);

        return ResponseEntity.ok()
                .body(Api.OK(UserResponseCode.LOGOUT_SUCCESS, null));
    }

    @PostMapping("/check-userId")
    @Operation(summary = "아이디 중복 체크", description = "아이디 중복을 체크합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST",content = @Content(schema = @Schema(implementation = Api.class))),
            @ApiResponse(responseCode = "404", description = "NOT FOUND", content = @Content(schema = @Schema(implementation = Api.class))),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR", content = @Content(schema = @Schema(implementation = Api.class)))
    })
    @Parameter(name = "userId", description = "유저 아이디", example = "user01")
    public ResponseEntity<Api> checkUserId(@RequestBody UserIdRequestDto dto){

        UserResponseCode code = userAccountService.checkUserId(dto.getUserId());
        return ResponseEntity.ok()
                .body(Api.OK(code, code.getStatus()));
    }

    @PostMapping("/check-nickname")
    @Operation(summary = "닉네임 중복 체크", description = "닉네임 중복 체크")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST",content = @Content(schema = @Schema(implementation = Api.class))),
            @ApiResponse(responseCode = "404", description = "NOT FOUND", content = @Content(schema = @Schema(implementation = Api.class))),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR", content = @Content(schema = @Schema(implementation = Api.class)))
    })
    @Parameter(name = "nickname", description = "닉네임", example = "nick01")
    public ResponseEntity<?> checkNickname(@RequestBody UserNicknameRequestDto dto){
        ResponseCode code = userAccountService.checkNickname(dto.getNickname());

        return ResponseEntity.ok()
                .body(Api.OK(code, code.getStatus()));
    }


}
