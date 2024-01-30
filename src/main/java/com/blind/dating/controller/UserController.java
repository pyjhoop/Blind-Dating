package com.blind.dating.controller;

import com.blind.dating.common.code.UserResponseCode;
import com.blind.dating.domain.UserAccount;
import com.blind.dating.common.Api;
import com.blind.dating.dto.user.UserInfoWithPageInfo;
import com.blind.dating.dto.user.UserUpdateRequestDto;
import com.blind.dating.dto.user.UserWithInterestAndQuestionDto;
import com.blind.dating.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@Tag(name = "User Info", description = "유저 조회 서비스")
@SecurityRequirement(name = "Bearer Authentication")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class UserController {

    private final UserService userService;

    // 유저10명 불러오기
    @Operation(summary = "추천 유저 조회", description = "추천 유저 10명을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST",content = @Content(schema = @Schema(implementation = Api.class))),
            @ApiResponse(responseCode = "404", description = "NOT FOUND", content = @Content(schema = @Schema(implementation = Api.class))),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR", content = @Content(schema = @Schema(implementation = Api.class)))
    })
    @GetMapping("/user-list")
    public ResponseEntity<?> getUserList(
            Authentication authentication,
            @PageableDefault(size = 10, sort = "recentLogin", direction = Sort.Direction.DESC) Pageable pageable
            ){

        // 이성 추천 유저리스트 가져오기.
        Page<UserWithInterestAndQuestionDto> pages = userService.getUserList(authentication,pageable);
        UserInfoWithPageInfo content = new UserInfoWithPageInfo(pages.getNumber(), pages.getTotalPages(), pages.getContent());

        return ResponseEntity.ok()
                .body(Api.OK(UserResponseCode.GET_USER_LIST_SUCCESS, content));
    }

    @GetMapping("/user")
    @Operation(summary = "내 정보 조회", description = "내 정보를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST",content = @Content(schema = @Schema(implementation = Api.class))),
            @ApiResponse(responseCode = "404", description = "NOT FOUND", content = @Content(schema = @Schema(implementation = Api.class))),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR", content = @Content(schema = @Schema(implementation = Api.class)))
    })
    public ResponseEntity<?> getMyInfo(
            Authentication authentication
    ){
        // 내정보 조회하기
        UserWithInterestAndQuestionDto userInfo = userService.getMyInfo(authentication);

        return ResponseEntity.ok()
                .body(Api.OK(UserResponseCode.GET_USER_INFO_SUCCESS, userInfo));
    }

    @PutMapping("/user")
    @Operation(summary = "내 정보 수정", description = "내 정보를 수정합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST",content = @Content(schema = @Schema(implementation = Api.class))),
            @ApiResponse(responseCode = "404", description = "NOT FOUND", content = @Content(schema = @Schema(implementation = Api.class))),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR", content = @Content(schema = @Schema(implementation = Api.class)))
    })
    public ResponseEntity<Api<UserWithInterestAndQuestionDto>> updateMyInfo(
            Authentication authentication
            , @RequestBody UserUpdateRequestDto dto
    ){
        //내정보 업데이트
        UserAccount user = userService.updateMyInfo(authentication, dto);
        // 업데이트 된 정보 가져오기
        UserWithInterestAndQuestionDto userInfo = UserWithInterestAndQuestionDto.from(user);

        return ResponseEntity.ok()
                .body(Api.OK(UserResponseCode.UPDATE_USER_INFO_SUCCESS, userInfo));
    }

}
