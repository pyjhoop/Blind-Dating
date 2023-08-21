package com.blind.dating.controller;

import com.blind.dating.domain.LikesUnlikes;
import com.blind.dating.dto.response.ResponseDto;
import com.blind.dating.dto.user.UserReceiverId;
import com.blind.dating.service.LikesUnlikesService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Like & Unlike", description = "좋아요, 싫어요 서비스")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class LikesUnlikesController {

    private final LikesUnlikesService likesUnlikesService;

    @PostMapping("/like")
    @Operation(summary = "좋아요", description = "좋아요 버튼 클릭시 상태에 따라 좋아요 상태를 반환한다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST",content = @Content(schema = @Schema(implementation = ResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "NOT FOUND", content = @Content(schema = @Schema(implementation = ResponseDto.class))),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR", content = @Content(schema = @Schema(implementation = ResponseDto.class)))

    })
    public ResponseEntity<ResponseDto> likeUser(
            @RequestBody UserReceiverId userReceiverId,
            Authentication authentication
    ){

        ResponseDto<LikesUnlikes> result = likesUnlikesService.likeUser(authentication,userReceiverId.getReceiverId());
        String message = "";
        if(result.getData().getIsLike()== null){
            message = "좋아요 취소";
        }else{
            message = "좋아요 성공";
        }
        return ResponseEntity.<ResponseDto>status(HttpStatus.OK)
                .body(ResponseDto.builder().status("OK")
                        .message(message)
                        .data(result.getMessage()).build());

    }

    @PostMapping("/unlike")
    @Operation(summary = "싫어요", description = "싫어요 버튼 클릭시 상태에 따라 싫어요 상태를 반환한다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST",content = @Content(schema = @Schema(implementation = ResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "NOT FOUND", content = @Content(schema = @Schema(implementation = ResponseDto.class))),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR", content = @Content(schema = @Schema(implementation = ResponseDto.class)))

    })
    public ResponseEntity<ResponseDto> unlikeUser(
            @RequestBody UserReceiverId userReceiverId,
            Authentication authentication
    ){

        LikesUnlikes result = likesUnlikesService.unlikeUser(authentication,userReceiverId.getReceiverId());
        String message = "";
        if(result.getIsLike()== null){
            message = "싫어요 취소";
        }else{
            message = "싫어요 성공";
        }
        return ResponseEntity.<ResponseDto>status(HttpStatus.OK)
                .body(ResponseDto.builder().status("OK")
                        .message(message).build());
    }

}
