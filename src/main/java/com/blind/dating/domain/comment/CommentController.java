package com.blind.dating.domain.comment;

import com.blind.dating.common.Api;
import com.blind.dating.common.code.CommentResponseCode;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class CommentController {
    private final CommentService commentService;


    @PostMapping("/comments")
    public ResponseEntity<?> createComment(
            Authentication authentication,
            @RequestBody CommentRequestDto commentRequestDto
    ) {
        commentService.createComment(authentication, commentRequestDto);
        return ResponseEntity.ok()
                .body(Api.OK(CommentResponseCode.COMMENT_CREATE_SUCCESS));
    }
    @PatchMapping("/comments/{commentId}")
    public ResponseEntity<?> updateComment(
            Authentication authentication,
            @PathVariable Long commentId,
            @Parameter(required = true)
            String content

    ){
        commentService.updateComment(authentication, commentId, content);

        return ResponseEntity.ok()
                .body(Api.OK(CommentResponseCode.COMMENT_UPDATE_SUCCESS));
    }
    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<?> deleteComment(
            Authentication authentication,
            @PathVariable Long commentId
    ) {
        commentService.deleteComment(authentication, commentId);
        return ResponseEntity.ok()
                .body(Api.OK(CommentResponseCode.COMMENT_DELETE_SUCCESS));
    }
}
