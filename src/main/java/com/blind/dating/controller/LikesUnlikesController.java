package com.blind.dating.controller;

import com.blind.dating.common.Api;
import com.blind.dating.common.code.LikesUnlikesResponseCode;
import com.blind.dating.dto.user.UserReceiverId;
import com.blind.dating.service.LikesUnlikesService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Like & Unlike", description = "좋아요, 싫어요 서비스")
@SecurityRequirement(name = "Bearer Authentication")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class LikesUnlikesController {

    private final LikesUnlikesService likesUnlikesService;

    @PostMapping("/like")
    @Operation(summary = "좋아요", description = "좋아요 버튼 클릭시 상태에 따라 좋아요 상태를 반환한다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST",content = @Content(schema = @Schema(implementation = Api.class))),
            @ApiResponse(responseCode = "404", description = "NOT FOUND", content = @Content(schema = @Schema(implementation = Api.class))),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR", content = @Content(schema = @Schema(implementation = Api.class)))

    })
    public ResponseEntity<?> likeUser(
            @RequestBody UserReceiverId userReceiverId,
            Authentication authentication
    ){

        Boolean result = likesUnlikesService.likeUser(authentication,userReceiverId.getReceiverId());
        
        return ResponseEntity.ok()
                .body(Api.OK(LikesUnlikesResponseCode.LIKE_SUCCESS, result));
    }

    @PostMapping("/unlike")
    @Operation(summary = "싫어요", description = "싫어요 버튼 클릭시 상태에 따라 싫어요 상태를 반환한다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST",content = @Content(schema = @Schema(implementation = Api.class))),
            @ApiResponse(responseCode = "404", description = "NOT FOUND", content = @Content(schema = @Schema(implementation = Api.class))),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR", content = @Content(schema = @Schema(implementation = Api.class)))

    })
    public ResponseEntity<?> unlikeUser(
            @RequestBody UserReceiverId userReceiverId,
            Authentication authentication
    ){
       likesUnlikesService.unlikeUser(authentication,userReceiverId.getReceiverId());

        return ResponseEntity.ok()
                .body(Api.OK(LikesUnlikesResponseCode.UNLIKE_SUCCESS, true));
    }

}
