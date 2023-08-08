package com.blind.dating.controller;

import com.blind.dating.domain.LikesUnlikes;
import com.blind.dating.dto.response.ResponseDto;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Like & Unlike", description = "좋아요, 싫어요 서비스")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class LikesUnlikesController {

    private final LikesUnlikesService likesUnlikesService;

    @GetMapping("/like/{receiverId}")
    @Operation(summary = "좋아요", description = "좋아요 버튼 클릭시 상태에 따라 좋아요 상태를 반환한다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST",content = @Content(schema = @Schema(implementation = ResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "NOT FOUND", content = @Content(schema = @Schema(implementation = ResponseDto.class))),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR", content = @Content(schema = @Schema(implementation = ResponseDto.class)))

    })
    public ResponseEntity<ResponseDto> likeUser(
            @PathVariable String receiverId,
            Authentication authentication
    ){

        LikesUnlikes result = likesUnlikesService.likeUser(authentication,receiverId);
        String message = "";
        if(result.getIsLike()== null){
            message = "좋아요 취소";
        }else{
            message = "좋아요 성공";
        }
        return ResponseEntity.<ResponseDto>status(HttpStatus.OK)
                .body(ResponseDto.builder().status("OK")
                        .message(message).build());

    }

    @GetMapping("/unlike/{receiverId}")
    @Operation(summary = "싫어요", description = "싫어요 버튼 클릭시 상태에 따라 싫어요 상태를 반환한다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST",content = @Content(schema = @Schema(implementation = ResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "NOT FOUND", content = @Content(schema = @Schema(implementation = ResponseDto.class))),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR", content = @Content(schema = @Schema(implementation = ResponseDto.class)))

    })
    public ResponseEntity<ResponseDto> unlikeUser(
            @PathVariable String receiverId,
            Authentication authentication
    ){

        LikesUnlikes result = likesUnlikesService.unlikeUser(authentication,receiverId);
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
