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
import com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper;
import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.epages.restdocs.apispec.Schema;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

import static com.epages.restdocs.apispec.ResourceDocumentation.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("PostController - 테스트")
@WebMvcTest(PostController.class)
@Import(SecurityConfig.class)
@AutoConfigureMockMvc(addFilters = false)
class PostControllerTest extends ControllerTestConfig{

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
        post.setCreatedAt(LocalDateTime.now());
        authentication = new UsernamePasswordAuthenticationToken("1",null);
        postResponseDto = PostResponseDto.From(post);
    }

    @Test
    @DisplayName("post 생성 - 성공")
    @WithMockUser(username = "1")
    void givenPostRequest_whenCreatePost_thenReturn200() throws Exception {
        // Given
        given(postService.createPost(userId, request)).willReturn(postResponseDto);
        ResultActions actions = mvc.perform(
                RestDocumentationRequestBuilders.post("/api/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .accept(MediaType.APPLICATION_JSON)
                        .principal(authentication)
                        .header("Authorization", "Bearer "+"accessToken")
        ).andDo(
                MockMvcRestDocumentationWrapper.document("게시글 생성 - 성공",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        resource(
                                ResourceSnippetParameters.builder()
                                        .description("게시글을 생성")
                                        .tag("Post").summary("게시글 t")
                                        .requestFields(
                                            fieldWithPath("title").description("게시글 제목"),
                                            fieldWithPath("content").description("게시글 내용")
                                        ).requestHeaders(
                                                headerWithName("Authorization").description("Basic auth credentials")
                                        ).responseFields(
                                            fieldWithPath("code").description("응답 코드"),
                                            fieldWithPath("status").description("응답 상태"),
                                            fieldWithPath("message").description("응답 메시지"),
                                            fieldWithPath("data").description("응답 데이터"),
                                            fieldWithPath("data.id").description("게시글 번호"),
                                            fieldWithPath("data.user.id").description("게시글 작성자 번호"),
                                            fieldWithPath("data.user.nickname").description("게시글 작성자 닉네임"),
                                            fieldWithPath("data.title").description("게시글 제목"),
                                            fieldWithPath("data.content").description("게시글 내용"),
                                            fieldWithPath("data.hit").description("게시글 추천수"),
                                            fieldWithPath("data.view").description("게시글 조회수"),
                                            fieldWithPath("data.createdAt").description("게시글 생성일")
                                        ).responseSchema(Schema.schema("게시물 생성 응답")).build()
                        )
                )
        );
        actions.andExpect(status().isOk());
    }


    @Test
    @DisplayName("post 수정 - 성공")
    @WithMockUser(username = "1")
    void givenPostRequest_whenUpdatePost_thenReturn200() throws Exception {
        // Given
        post.setTitle("수정된 제목");
        given(postService.updatePost(postId, request)).willReturn(postResponseDto);

        // When
        ResultActions actions = mockMvc.perform(
                RestDocumentationRequestBuilders.patch("/api/posts/{postId}",postId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .accept(MediaType.APPLICATION_JSON)
                        .principal(authentication)
                        .header("Authorization", "Bearer "+"accessToken")
        ).andDo(
                MockMvcRestDocumentationWrapper.document("게시글 수정 - 성공",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        resource(
                                ResourceSnippetParameters.builder()
                                        .description("게시글을 수정")
                                        .tag("Post").summary("게시글 수정 API")
                                        .requestFields(
                                                fieldWithPath("title").description("게시글 제목"),
                                                fieldWithPath("content").description("게시글 내용")
                                        ).requestHeaders(
                                                headerWithName("Authorization").description("Basic auth credentials")
                                        ).responseFields(
                                                fieldWithPath("code").description("응답 코드"),
                                                fieldWithPath("status").description("응답 상태"),
                                                fieldWithPath("message").description("응답 메시지"),
                                                fieldWithPath("data").description("응답 데이터"),
                                                fieldWithPath("data.id").description("게시글 번호"),
                                                fieldWithPath("data.user.id").description("게시글 작성자 번호"),
                                                fieldWithPath("data.user.nickname").description("게시글 작성자 닉네임"),
                                                fieldWithPath("data.title").description("게시글 제목"),
                                                fieldWithPath("data.content").description("게시글 내용"),
                                                fieldWithPath("data.hit").description("게시글 추천수"),
                                                fieldWithPath("data.view").description("게시글 조회수"),
                                                fieldWithPath("data.createdAt").description("게시글 생성일")
                                        ).responseSchema(Schema.schema("게시물 수정 응답")).build()
                        )
                )
        );
        actions.andExpect(status().isOk());
    }
    
    @Test
    @DisplayName("post 단건 조회 - 성공")
    @WithMockUser(username = "1")
    void givenPostRequest_whenGetPost_thenReturn200() throws Exception {
        // Given
        given(postService.getPost(postId)).willReturn(postResponseDto);

        // When
        ResultActions actions = mockMvc.perform(
                RestDocumentationRequestBuilders.get("/api/posts/{postId}",postId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .principal(authentication)
                        .header("Authorization", "Bearer "+"accessToken")
        ).andDo(
                MockMvcRestDocumentationWrapper.document("게시글 단건 조회 - 성공",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        resource(
                                ResourceSnippetParameters.builder()
                                        .description("게시글을 단건 조회")
                                        .tag("Post").summary("게시글 단건 조회 API")
                                        .requestFields()
                                        .requestHeaders(
                                                headerWithName("Authorization").description("Basic auth credentials")
                                        ).responseFields(
                                                fieldWithPath("code").description("응답 코드"),
                                                fieldWithPath("status").description("응답 상태"),
                                                fieldWithPath("message").description("응답 메시지"),
                                                fieldWithPath("data").description("응답 데이터"),
                                                fieldWithPath("data.id").description("게시글 번호"),
                                                fieldWithPath("data.user.id").description("게시글 작성자 번호"),
                                                fieldWithPath("data.user.nickname").description("게시글 작성자 닉네임"),
                                                fieldWithPath("data.title").description("게시글 제목"),
                                                fieldWithPath("data.content").description("게시글 내용"),
                                                fieldWithPath("data.hit").description("게시글 추천수"),
                                                fieldWithPath("data.view").description("게시글 조회수"),
                                                fieldWithPath("data.createdAt").description("게시글 생성일")
                                        ).responseSchema(Schema.schema("게시물 단건 조회 응답")).build()
                        )
                )
        );
        actions.andExpect(status().isOk());
    }


//    @Test
//    @DisplayName("post 여러건 조회 - 성공")
//    @WithMockUser(username = "1")
//    void givenPostRequest_whenGetPosts_thenReturn200() throws Exception {
//        // Given
//        Pageable pageable = PageRequest.of(0,1);
//        Page<Post> page = new PageImpl<>(List.of(post), pageable, 1);
//        List<PostResponseDto> dtoList = Stream.of(post).map(PostResponseDto::From).toList();
//        PageInfoWithPosts pageInfoWithPosts = new PageInfoWithPosts(0, 1, dtoList);
//
//        given(postService.getPosts(pageable)).willReturn(pageInfoWithPosts);
//
//        // When
//        ResultActions actions = mockMvc.perform(
//                RestDocumentationRequestBuilders.get("/api/posts")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .queryParam("size","20")
//                        .queryParam("page","0")
//                        .accept(MediaType.APPLICATION_JSON)
//                        .principal(authentication)
//                        .header("Authorization", "Bearer "+"accessToken")
//        ).andDo(
//                MockMvcRestDocumentationWrapper.document("게시글 단건 조회 - 성공",
//                        preprocessRequest(prettyPrint()),
//                        preprocessResponse(prettyPrint()),
//                        resource(
//                                ResourceSnippetParameters.builder()
//                                        .description("게시글을 여러건 조회")
//                                        .tag("Post").summary("게시글 관련 API")
//                                        .requestFields()
//                                        .queryParameters(
//                                                parameterWithName("size").description("조회 개수").optional(),
//                                                parameterWithName("page").description("조회 페이지").optional()
//                                        )
//                                        .requestHeaders(
//                                                headerWithName("Authorization").description("Basic auth credentials")
//                                        ).responseFields(
//                                                fieldWithPath("code").description("응답 코드"),
//                                                fieldWithPath("status").description("응답 상태"),
//                                                fieldWithPath("message").description("응답 메시지"),
//                                                fieldWithPath("data").description("응답 데이터"),
//                                                fieldWithPath("data.pageNumber").description("페이지 번호"),
//                                                fieldWithPath("data.totalPages").description("총 페이지 개수"),
//                                                fieldWithPath("data.posts").description("게시글 리스트"),
//                                                fieldWithPath("data.posts[].id").description("게시글 번호"),
//                                                fieldWithPath("data.posts[].user").description("게시글 작성자"),
//                                                fieldWithPath("data.posts[].user.id").description("게시글 작성자 번호"),
//                                                fieldWithPath("data.posts[].user.nickname").description("게시글 작성자 닉네임"),
//                                                fieldWithPath("data.posts[].title").description("게시글 제목"),
//                                                fieldWithPath("data.posts[].content").description("게시글 내용"),
//                                                fieldWithPath("data.posts[].hit").description("게시글 추천수"),
//                                                fieldWithPath("data.posts[].view").description("게시글 조회수"),
//                                                fieldWithPath("data.posts[].createdAt").description("게시글 생성일")
//                                        ).responseSchema(Schema.schema("게시물 여러건 조회 응답")).build()
//                        )
//                )
//        );
//        actions.andExpect(status().isOk());
//    }


}