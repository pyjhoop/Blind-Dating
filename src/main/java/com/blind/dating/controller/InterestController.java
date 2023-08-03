package com.blind.dating.controller;

import com.blind.dating.domain.Interest;
import com.blind.dating.domain.UserAccount;
import com.blind.dating.dto.InterestDto;
import com.blind.dating.dto.response.InterestResponse;
import com.blind.dating.dto.response.ResponseDto;
import com.blind.dating.service.InterestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Tag(name = "Interest Info", description = "관심사 관련 서비스")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/interest")
public class InterestController {

    private final InterestService interestService;

    // 내 관심사들 조회하기.
    @GetMapping
    @Operation(summary = "내 관심사 조회", description = "내 관심사를 조회한다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST",content = @Content(schema = @Schema(implementation = ResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "NOT FOUND", content = @Content(schema = @Schema(implementation = ResponseDto.class))),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR", content = @Content(schema = @Schema(implementation = ResponseDto.class)))
    })
    public ResponseDto<List<InterestResponse>> getMyInterests(Authentication authentication){

        UserAccount user =(UserAccount)authentication.getPrincipal();

        List<InterestResponse> interests = interestService.getInterests(user)
                .stream().map(InterestResponse::from).collect(Collectors.toList());

        return ResponseDto.<List<InterestResponse>>builder()
                .status("OK")
                .message("내 관심사가 성공적으로 조회되었습니다.")
                .data(interests).build();
    }

    // 관심사들 저장하기.
    @PostMapping
    @Operation(summary = "내 관심사 저장", description = "내 관심사를 저장합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST",content = @Content(schema = @Schema(implementation = ResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "NOT FOUND", content = @Content(schema = @Schema(implementation = ResponseDto.class))),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR", content = @Content(schema = @Schema(implementation = ResponseDto.class)))
    })
    @Parameter(name = "List<InterestDto>", description = "관심사 리스트")
    public ResponseDto<List<InterestResponse>> saveInterests(@RequestBody List<InterestDto> dtos,
                                       Authentication authentication){

        List<InterestResponse> list = interestService.saveInterest(authentication,dtos)
                .stream().map(InterestResponse::from).collect(Collectors.toList());

        return ResponseDto.<List<InterestResponse>>builder()
                .status("OK")
                .message("관심사가 성공적으로 저장되었습니다.")
                .data(list).build();
    }

    // 내 관심사 수정하기
    @PutMapping
    @Operation(summary = "내 관심사 수정", description = "내 관심사를 수정합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST",content = @Content(schema = @Schema(implementation = ResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "NOT FOUND", content = @Content(schema = @Schema(implementation = ResponseDto.class))),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR", content = @Content(schema = @Schema(implementation = ResponseDto.class)))
    })
    @Parameter(name = "List<InterestDto>", description = "관심사 리스트")
    public ResponseDto<List<InterestResponse>> updateInterests(@RequestBody List<InterestDto> dtos,
                                                               Authentication authentication){

        List<InterestResponse> list = interestService.updateInterest(authentication,dtos)
                .stream().map(InterestResponse::from).collect(Collectors.toList());

        return ResponseDto.<List<InterestResponse>>builder()
                .status("OK")
                .message("관심사가 성공적으로 저장되었습니다.")
                .data(list).build();

    }


}
