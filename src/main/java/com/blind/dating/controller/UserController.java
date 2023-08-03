package com.blind.dating.controller;

import com.blind.dating.domain.UserAccount;
import com.blind.dating.dto.response.ResponseDto;
import com.blind.dating.repository.UserAccountRepository;
import com.blind.dating.service.UserAccountService;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "추천 유저 Info", description = "추천 유저 조회 서비스")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class UserController {

    private final UserService userService;
    private final UserAccountRepository userAccountRepository;

    // 유저10명 불러오기

    @Operation(summary = "추천 유저 조회", description = "추천 유저 10명을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST",content = @Content(schema = @Schema(implementation = ResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "NOT FOUND", content = @Content(schema = @Schema(implementation = ResponseDto.class))),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR", content = @Content(schema = @Schema(implementation = ResponseDto.class)))
    })
    @GetMapping("/user-list")
    public ResponseDto<Page<UserAccount>> getUserList(
            Authentication authentication,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
            ){
        UserAccount user = (UserAccount) authentication.getPrincipal();
        String gender = user.getGender();
        int score = user.getScore();

        Page<UserAccount> users = null;

        if(gender.equals("M")){
            users = userService.getUserList(score, "W", pageable);
        }else{
            users = userService.getUserList(score, "M", pageable);
        }

        return ResponseDto.<Page<UserAccount>>builder()
                .status("OK")
                .message("성공적으로 조회되었습니다.")
                .data(users).build();

    }

}
