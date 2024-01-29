package com.blind.dating.controller;

import com.blind.dating.domain.UserAccount;
import com.blind.dating.dto.response.ResponseDto;
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
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
            @ApiResponse(responseCode = "400", description = "BAD REQUEST",content = @Content(schema = @Schema(implementation = ResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "NOT FOUND", content = @Content(schema = @Schema(implementation = ResponseDto.class))),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR", content = @Content(schema = @Schema(implementation = ResponseDto.class)))
    })
    @GetMapping("/user-list")
    public ResponseDto<UserInfoWithPageInfo> getUserList(
            Authentication authentication,
            @PageableDefault(size = 10, sort = "recentLogin", direction = Sort.Direction.DESC) Pageable pageable
            ){

        // 이성 추천 유저리스트 가져오기.
        Page<UserAccount> users = userService.getUserList(authentication,pageable);
        Page<UserWithInterestAndQuestionDto> pages = users.map(UserWithInterestAndQuestionDto::from);

        UserInfoWithPageInfo content = new UserInfoWithPageInfo();
        content.setContent(pages.getContent());
        content.setTotalPages(pages.getTotalPages());
        content.setPageNumber(pages.getNumber());


        return ResponseDto.<UserInfoWithPageInfo>builder()
                .status("OK")
                .message("성공적으로 조회되었습니다.")
                .data(content).build();
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
        // 내정보 조회하기
        UserAccount userAccount = userService.getMyInfo(authentication);
        UserWithInterestAndQuestionDto userInfo = UserWithInterestAndQuestionDto.from(userAccount);

        return ResponseDto.<UserWithInterestAndQuestionDto>builder()
                .status("OK")
                .message("내 정보가 성공적으로 조회되었습니다.")
                .data(userInfo).build();
    }

    @PutMapping("/user")
    @Operation(summary = "내 정보 수정", description = "내 정보를 수정합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST",content = @Content(schema = @Schema(implementation = ResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "NOT FOUND", content = @Content(schema = @Schema(implementation = ResponseDto.class))),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR", content = @Content(schema = @Schema(implementation = ResponseDto.class)))
    })
    public ResponseEntity<ResponseDto<UserWithInterestAndQuestionDto>> updateMyInfo(
            Authentication authentication
            , @RequestBody UserUpdateRequestDto dto
    ){
        //내정보 업데이트
        UserAccount user = userService.updateMyInfo(authentication, dto);
        // 업데이트 된 정보 가져오기
        UserAccount userAccount = userService.getMyInfo(authentication);
        UserWithInterestAndQuestionDto userInfo = UserWithInterestAndQuestionDto.from(userAccount);

        return ResponseEntity.<ResponseDto<UserWithInterestAndQuestionDto>>ok()
                .body(ResponseDto.<UserWithInterestAndQuestionDto>builder()
                        .status("OK")
                        .message("내 정보가 성공적으로 수정되었습니다.")
                        .data(userInfo)
                        .build());


    }

}
