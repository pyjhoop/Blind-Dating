package com.blind.dating.controller;

import com.blind.dating.domain.UserAccount;
import com.blind.dating.dto.response.ResponseDto;
import com.blind.dating.dto.user.UserWithInterestAndQuestionDto;
import com.blind.dating.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@Tag(name = "User Info", description = "유저 조회 서비스")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class UserController {

    private final UserService userService;

    // 유저10명 불러오기
    @Operation(summary = "추천 유저 조회", description = "추천 유저 10명을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST",content = @Content(schema = @Schema(implementation = ResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "NOT FOUND", content = @Content(schema = @Schema(implementation = ResponseDto.class))),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR", content = @Content(schema = @Schema(implementation = ResponseDto.class)))
    })
    @GetMapping("/user-list")
    public ResponseDto<Page<UserWithInterestAndQuestionDto>> getUserList(
            Authentication authentication,
            @PageableDefault(size = 10, sort = "recentLogin", direction = Sort.Direction.DESC) Pageable pageable
            ){
        UserAccount user = (UserAccount) authentication.getPrincipal();
        String gender = user.getGender();

        Page<UserAccount> users = null;

        if(gender.equals("M")){
            users = userService.getUserList("W", authentication, pageable);
        }else{
            users = userService.getUserList("M", authentication, pageable);
        }
        Page<UserWithInterestAndQuestionDto> dtos = users.map(UserWithInterestAndQuestionDto::from);

        return ResponseDto.<Page<UserWithInterestAndQuestionDto>>builder()
                .status("OK")
                .message("성공적으로 조회되었습니다.")
                .data(dtos).build();
    }

    @GetMapping("/user")
    @Operation(summary = "내 정보 조회", description = "내 정보를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST",content = @Content(schema = @Schema(implementation = ResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "NOT FOUND", content = @Content(schema = @Schema(implementation = ResponseDto.class))),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR", content = @Content(schema = @Schema(implementation = ResponseDto.class)))
    })
    public ResponseDto<UserWithInterestAndQuestionDto> getMyInfo(
            Authentication authentication
    ){
        UserAccount user = (UserAccount) authentication.getPrincipal();
        String userId = user.getUserId();

        UserAccount userAccount = userService.getMyInfo(userId);

        UserWithInterestAndQuestionDto dto = UserWithInterestAndQuestionDto.from(userAccount);

        return ResponseDto.<UserWithInterestAndQuestionDto>builder()
                .status("OK")
                .message("내 정보가 성공적으로 조회되었습니다.")
                .data(dto).build();
    }

}
