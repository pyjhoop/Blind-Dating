package com.blind.dating.service;

import com.blind.dating.common.code.PostResponseCode;
import com.blind.dating.domain.post.Post;
import com.blind.dating.domain.user.Role;
import com.blind.dating.domain.post.PostService;
import com.blind.dating.domain.user.UserAccount;
import com.blind.dating.dto.post.PageInfoWithPosts;
import com.blind.dating.dto.post.PostRequestDto;
import com.blind.dating.dto.post.PostResponseDto;
import com.blind.dating.exception.ApiException;
import com.blind.dating.domain.post.PostRepository;
import com.blind.dating.domain.user.UserAccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
@DisplayName("Post Service - 테스트")
class PostServiceTest {

    @Mock private PostRepository postRepository;
    @Mock private UserAccountRepository userAccountRepository;
    @InjectMocks private PostService postService;

    private PostRequestDto request;
    private Long userId = 1L;
    private Long postId = 1L;
    private UserAccount user;
    private Post post;
    private Authentication authentication;

    @BeforeEach
    void setting() {
        request = new PostRequestDto("제목이야", "내용이야");
        user = new UserAccount(1L, "userId","password","nickname","서울","INTP","M",false,"하이요",null, Role.USER.getValue(),"K",null,null);
        post = new Post(1L, user, "제목이야", "내용이야", 0L, 0L);
        post.setCreatedAt(LocalDateTime.now());
        authentication = new UsernamePasswordAuthenticationToken("1","");
    }

    @Test
    @DisplayName("게시글 생성 - 성공")
    void givenUserIdWithRequest_whenCreatePost_thenSuccess() {
        // Given
        given(userAccountRepository.findById(userId)).willReturn(Optional.of(user));
        given(postRepository.save(any(Post.class))).willReturn(post);

        // When
        PostResponseDto result = postService.createPost(authentication, request);

        // Then
        assertThat(result).hasFieldOrPropertyWithValue("id", 1L);
        assertThat(result).hasFieldOrPropertyWithValue("title", post.getTitle());
        assertThat(result).hasFieldOrPropertyWithValue("content", post.getContent());
        assertThat(result).hasFieldOrPropertyWithValue("hit", post.getHit());
    }

    @Test
    @DisplayName("게시글 생성 - 실패")
    void givenUserIdWithRequest_whenCreatePost_thenThrowException() {
        // Given
        given(userAccountRepository.findById(userId)).willReturn(Optional.empty());

        // When
        ApiException exception = assertThrows(ApiException.class, ()-> {
            PostResponseDto result = postService.createPost(authentication, request);
        });

        // Then
        assertThat(exception.getResponseCode()).isEqualTo(PostResponseCode.CREATE_POST_FAil);
    }

    @Test
    @DisplayName("게시글 수정 - 성공")
    void givenUserIdWithRequest_whenUpdatePost_thenSuccess() {
        // Given
        given(postRepository.findById(postId)).willReturn(Optional.of(post));
        // When
        PostResponseDto result = postService.updatePost(postId, request);

        // Then
        assertThat(result).hasFieldOrPropertyWithValue("id", 1L);
        assertThat(result).hasFieldOrPropertyWithValue("title", post.getTitle());
        assertThat(result).hasFieldOrPropertyWithValue("content", post.getContent());
        assertThat(result).hasFieldOrPropertyWithValue("hit", post.getHit());
    }

    @Test
    @DisplayName("게시글 수정 - 실패")
    void givenUserIdWithRequest_whenUpdatePost_thenThrowException() {
        // Given
        given(postRepository.findById(postId)).willReturn(Optional.empty());
        // When
        ApiException exception = assertThrows(ApiException.class, () -> {
            PostResponseDto result = postService.updatePost(postId, request);
        });

        // Then
        assertThat(exception.getResponseCode()).isEqualTo(PostResponseCode.POST_NOT_FOUNT);
    }

    @Test
    @DisplayName("게시글 조회 - 성공")
    void givenUserIdWithRequest_whenGetPost_thenSuccess() {
        // Given
        given(postRepository.findPost(postId)).willReturn(Optional.of(post));
        // When
        PostResponseDto result = postService.getPost(postId);

        // Then
        assertThat(result).hasFieldOrPropertyWithValue("id", 1L);
        assertThat(result).hasFieldOrPropertyWithValue("title", post.getTitle());
        assertThat(result).hasFieldOrPropertyWithValue("content", post.getContent());
        assertThat(result).hasFieldOrPropertyWithValue("hit", post.getHit());
        assertThat(result).hasFieldOrPropertyWithValue("view", 1L);
    }

    @Test
    @DisplayName("게시글 조회 - 실패")
    void givenUserIdWithRequest_whenGetPost_thenThrowException() {
        // Given
        given(postRepository.findPost(postId)).willReturn(Optional.empty());
        // When
        ApiException exception = assertThrows(ApiException.class, ()->{
            PostResponseDto result = postService.getPost(postId);
        });

        // Then
        assertThat(exception.getResponseCode()).isEqualTo(PostResponseCode.POST_NOT_FOUNT);
    }

    @Test
    @DisplayName("게시글 여러개 조회 - 성공")
    void givenUserIdWithRequest_whenGetPosts_thenSuccess() {
        // Given
        Pageable pageable = PageRequest.of(0,1);
        Page<Post> page = new PageImpl<>(List.of(post), pageable, 1);
        given(postRepository.findAll(pageable)).willReturn(page);
        // When
        PageInfoWithPosts result = postService.getPosts(pageable);

        // Then
        assertThat(result).hasFieldOrPropertyWithValue("pageNumber", 0);
        assertThat(result).hasFieldOrPropertyWithValue("totalPages", 1);
    }


}