package com.blind.dating.domain.post;

import com.blind.dating.common.Api;
import com.blind.dating.common.code.PostResponseCode;
import com.blind.dating.dto.post.PageInfoWithPosts;
import com.blind.dating.dto.post.PostRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class PostController {
    private final PostService postService;

    @PostMapping("/posts")
    public ResponseEntity<?> createPost
    (
        Authentication authentication,
        @RequestBody PostRequestDto request
    ) {
        PostResponseDto result = postService.createPost(authentication, request);
        return ResponseEntity.ok()
                .body(Api.OK(PostResponseCode.CREATE_POST_SUCCESS, result));
    }
    @PatchMapping("/posts/{postId}")
    public ResponseEntity<?> updatePost
    (
        @RequestBody PostRequestDto request,
        @PathVariable Long postId
    ){
        PostResponseDto result = postService.updatePost(postId, request);

        return ResponseEntity.ok()
                .body(Api.OK(PostResponseCode.UPDATE_POST_SUCCESS, result));
    }

    @GetMapping("/posts/{postId}")
    public ResponseEntity<?> getPost
    (
        @PathVariable Long postId
    ) {
        PostResponseWithCommentDto result = postService.getPost(postId);

        return ResponseEntity.ok()
                .body(Api.OK(PostResponseCode.GET_POST_SUCCESS, result));
    }

    @GetMapping("/posts")
    public ResponseEntity<?> getPosts(
            @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.ASC)
            Pageable pageable
    ){
        PageInfoWithPosts result = postService.getPosts(pageable);

        return ResponseEntity.ok()
                .body(Api.OK(PostResponseCode.GET_POST_SUCCESS, result));
    }

    @DeleteMapping("/posts/{postId}")
    public ResponseEntity<?> deletePosts(
            @PathVariable Long postId
    ) {
        return null;
    }



}
