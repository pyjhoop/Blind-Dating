package com.blind.dating.controller;

import com.blind.dating.common.code.PostResponseCode;
import com.blind.dating.config.SecurityConfig;
import com.blind.dating.domain.Post;
import com.blind.dating.domain.Role;
import com.blind.dating.domain.UserAccount;
import com.blind.dating.dto.post.PageInfoWithPosts;
import com.blind.dating.dto.post.PostRequestDto;
import com.blind.dating.dto.post.PostResponseDto;
import com.blind.dating.security.TokenProvider;
import com.blind.dating.service.PostService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("PostController - 테스트")
@WebMvcTest(PostController.class)
@Import(SecurityConfig.class)
class PostControllerTest {

    @MockBean private PostService postService;
    @MockBean private TokenProvider tokenProvider;
    @Autowired private MockMvc mvc;
    @Autowired private ObjectMapper objectMapper;

    private PostRequestDto request;
    private Long userId = 1L;
    private Long postId = 1L;
    private UserAccount user;
    private Post post;
    private Authentication authentication;
    private PostResponseDto postResponseDto;

    @BeforeEach
    void setting() {
        request = new PostRequestDto("제목이야", "내용이야");
        user = new UserAccount(1L, "userId","password","nickname","서울","INTP","M",false,"하이요",null, Role.USER.getValue(),"K",null,null,null,null);
        post = new Post(1L, user, "제목이야", "내용이야", 0L, 0L);
        authentication = new UsernamePasswordAuthenticationToken("1",null);
        postResponseDto = PostResponseDto.From(post);
    }

    @Test
    @DisplayName("post 생성 - 성공")
    @WithMockUser(username = "1")
    void givenPostRequest_whenCreatePost_thenReturn200() throws Exception {
        // Given
        given(postService.createPost(userId, request)).willReturn(postResponseDto);

        // When
        mvc.perform(get("/api/posts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("게시글이 성공적으로 조회되었습니다."));
    }

    @Test
    @DisplayName("post 생성 - 성공")
    void givenPostRequest_whenCreatePost_thenReturn401() throws Exception {
        // Given
        given(postService.createPost(userId, request)).willReturn(postResponseDto);

        // When
        mvc.perform(get("/api/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().is(401));

        verify(postService, never()).createPost(anyLong(), any(PostRequestDto.class));
    }

    @Test
    @DisplayName("post 수정 - 성공")
    @WithMockUser(username = "1")
    void givenPostRequest_whenUpdatePost_thenReturn200() throws Exception {
        // Given
        post.setTitle("수정된 제목");
        given(postService.updatePost(postId, request)).willReturn(postResponseDto);

        // When
        mvc.perform(patch("/api/posts/{postId}",postId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.data.id").value(1L));
    }

    @Test
    @DisplayName("post 수정 - 실패")
    void givenPostRequest_whenUpdatePost_thenReturn401() throws Exception {
        // Given
        post.setTitle("수정된 제목");
        given(postService.updatePost(postId, request)).willReturn(postResponseDto);

        // When
        mvc.perform(patch("/api/posts/{postId}",postId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().is(401));
    }

    @Test
    @DisplayName("post 단건 조회 - 성공")
    @WithMockUser(username = "1")
    void givenPostRequest_whenGetPost_thenReturn200() throws Exception {
        // Given
        given(postService.getPost(postId)).willReturn(postResponseDto);

        // When
        mvc.perform(get("/api/posts/{postId}",postId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value(PostResponseCode.GET_POST_SUCCESS.getMessage()));
    }

    @Test
    @DisplayName("post 단건 조회 - 성공")
    void givenPostRequest_whenGetPost_thenReturn401() throws Exception {
        // Given
        given(postService.getPost(postId)).willReturn(postResponseDto);

        // When
        mvc.perform(get("/api/posts/{postId}",postId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().is(401));
    }

    @Test
    @DisplayName("post 여러건 조회 - 성공")
    @WithMockUser(username = "1")
    void givenPostRequest_whenGetPosts_thenReturn200() throws Exception {
        // Given
        Pageable pageable = PageRequest.of(0,1);
        Page<Post> page = new PageImpl<>(List.of(post), pageable, 1);
        List<PostResponseDto> dtoList = page.getContent().stream().map(PostResponseDto::From).toList();
        PageInfoWithPosts pageInfoWithPosts = new PageInfoWithPosts(page.getNumber(), page.getTotalPages(), dtoList);

        given(postService.getPosts(pageable)).willReturn(pageInfoWithPosts);

        // When
        mvc.perform(get("/api/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value(PostResponseCode.GET_POST_SUCCESS.getMessage()));
    }

    @Test
    @DisplayName("post 여러건 조회 - 실패")
    void givenPostRequest_whenGetPosts_thenReturn401() throws Exception {
        // Given
        Pageable pageable = PageRequest.of(0,1);
        Page<Post> page = new PageImpl<>(List.of(post), pageable, 1);
        List<PostResponseDto> dtoList = page.getContent().stream().map(PostResponseDto::From).toList();
        PageInfoWithPosts pageInfoWithPosts = new PageInfoWithPosts(page.getNumber(), page.getTotalPages(), dtoList);

        given(postService.getPosts(pageable)).willReturn(pageInfoWithPosts);

        // When
        mvc.perform(get("/api/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().is(401));
    }
    







}