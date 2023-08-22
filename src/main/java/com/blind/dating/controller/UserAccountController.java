package com.blind.dating.controller;

import com.blind.dating.domain.UserAccount;
import com.blind.dating.dto.interest.InterestResponse;
import com.blind.dating.dto.user.*;
import com.blind.dating.dto.response.ResponseDto;
import com.blind.dating.security.TokenProvider;
import com.blind.dating.service.InterestService;
import com.blind.dating.service.QuestionService;
import com.blind.dating.service.UserAccountService;
import io.jsonwebtoken.ExpiredJwtException;
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
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Tag(name = "UserAccount Info", description = "인증 관련 서비스")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class UserAccountController {

    private final UserAccountService userAccountService;
    private final TokenProvider tokenProvider;
    private final InterestService interestService;
    private final QuestionService questionService;

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
    public ResponseEntity<ResponseDto> registerUser(
            @RequestBody @Valid UserRequestDto dto,
            Errors errors
            ){
        if (errors.hasErrors()) {

            // 유효성 통과 못한 필드와 메시지를 핸들링
            Map<String, String> validatorResult = userAccountService.validateHandling(errors);
            return ResponseEntity.<ResponseDto>badRequest()
                    .body(ResponseDto.builder()
                            .status("BAD REQUEST")
                            .message("회원가입에 실패했습니다.")
                            .data(validatorResult)
                            .build());

        }


        //TODO 필수데이터가 부족할때 통합 예외처리해주기
        String accessToken = tokenProvider.create(dto.toEntity());
        String refreshToken = tokenProvider.refreshToken(dto.toEntity());
        UserAccount user = userAccountService.create(dto, refreshToken);

        List<String> interests = dto.getInterests();
        List<Boolean> questions = dto.getQuestions();
        questionService.saveQuestions(user,questions);


        List<InterestResponse> list = interestService.saveInterest(user,interests)
                .stream().map(InterestResponse::from).collect(Collectors.toList());

        TokenDto tokens = TokenDto.builder().refreshToken(refreshToken)
                .accessToken(accessToken).build();




        return ResponseEntity.<ResponseDto>ok()
                .body(ResponseDto.builder()
                        .status("OK")
                        .message("회원가입이 성공적으로 완료되었습니다.")
                        .data(UserResponse.from(user, tokens))
                        .build());

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
        log.info("userId={}",dto.getUserId());

        if(user == null){
            return ResponseDto.<UserResponse>builder()
                    .status("BAD REQUEST")
                    .message("로그인이 실패했습니다.")
                    .data(null)
                    .build();

        }else{
            TokenDto tokens = TokenDto.builder()
                    .accessToken(tokenProvider.create(user))
                    .refreshToken(user.getRefreshToken()).build();


            return ResponseDto.<UserResponse>builder()
                    .status("OK")
                    .message("로그인이 성공적으로 처리되었습니다.")
                    .data(UserResponse.from(user, tokens))
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
    public ResponseDto<Boolean> checkUserId(@RequestBody UserIdRequestDto dto){

        boolean check = userAccountService.checkUserId(dto.getUserId());

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

    @PostMapping("/check-nickname")
    @Operation(summary = "닉네임 중복 체크", description = "닉네임 중복 체크")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST",content = @Content(schema = @Schema(implementation = ResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "NOT FOUND", content = @Content(schema = @Schema(implementation = ResponseDto.class))),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR", content = @Content(schema = @Schema(implementation = ResponseDto.class)))
    })
    @Parameter(name = "nickname", description = "닉네임", example = "nick01")
    public ResponseDto<Boolean> checkNickname(@RequestBody UserNicknameRequestDto dto){
        boolean check = userAccountService.checkNickname(dto.getNickname());

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
