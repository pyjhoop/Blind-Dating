package com.blind.dating.controller;

import com.blind.dating.common.code.UserResponseCode;
import com.blind.dating.config.SecurityConfig;
import com.blind.dating.domain.comment.Comment;
import com.blind.dating.domain.post.*;
import com.blind.dating.domain.user.Role;
import com.blind.dating.domain.user.UserAccount;
import com.blind.dating.dto.post.PageInfoWithPosts;
import com.blind.dating.dto.post.PostRequestDto;
import com.blind.dating.exception.ApiException;
import com.blind.dating.security.TokenProvider;
import com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper;
import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.epages.restdocs.apispec.Schema;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

import static com.epages.restdocs.apispec.ResourceDocumentation.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("PostController - 테스트")
@WebMvcTest(PostController.class)
@Import(SecurityConfig.class)
@ActiveProfiles("test")
class PostControllerTest extends ControllerTestConfig{

    @MockBean private PostService postService;
    @SpyBean private TokenProvider tokenProvider;
    @Autowired private MockMvc mvc;
    @Autowired private ObjectMapper objectMapper;

    private PostRequestDto request;
    private Long userId = 1L;
    private Long postId = 1L;
    private UserAccount user;
    private Post post;
    private Authentication authentication;
    private PostResponseDto postResponseDto;
    private PostResponseWithCommentDto postResponseWithCommentDto;
    private String accessToken;
    private Comment comment;

    @BeforeEach
    void setting() {
        request = new PostRequestDto("제목이야", "내용이야");
        user = new UserAccount(1L, "userId","password","nickname","서울","INTP","M",false,"하이요",null, Role.USER,"K","origin", "change", null);
        comment = new Comment(1L, "댓글1",true, user, post);
        comment.setCreatedAt(LocalDateTime.now());
        post = new Post(1L, user, "제목이야", "내용이야", 0L, 0L, List.of(comment));
        post.setCreatedAt(LocalDateTime.now());
        authentication = new UsernamePasswordAuthenticationToken("1",null);
        postResponseDto = PostResponseDto.From(post);
        accessToken = tokenProvider.create(user);
        postResponseWithCommentDto = PostResponseWithCommentDto.From(post);

    }

    @Nested
    @DisplayName("게시글 생성")
    class CreatePost {
    @Test
    @DisplayName("성공")
    void givenPostRequest_whenCreatePost_thenReturn200() throws Exception {
        // Given
        given(postService.createPost(any(Authentication.class), any(PostRequestDto.class))).willReturn(postResponseDto);

        // When
        mvc.perform(
                RestDocumentationRequestBuilders.post("/api/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer "+accessToken)
        ).andExpect(status().isOk())

        .andDo(
                MockMvcRestDocumentationWrapper.document("게시글 생성 - 성공",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        resource(
                                ResourceSnippetParameters.builder()
                                        .description("게시글 생성")
                                        .tag("Post").summary("게시글 생성 API")
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
                                                fieldWithPath("data.user").description("게시글 작성자 정보"),
                                                fieldWithPath("data.user.id").description("작성자 번호"),
                                                fieldWithPath("data.user.nickname").description("작성자 닉네임"),
                                                fieldWithPath("data.title").description("제목"),
                                                fieldWithPath("data.content").description("내용"),
                                                fieldWithPath("data.hit").description("추천 수"),
                                                fieldWithPath("data.view").description("좋아요 수"),
                                                fieldWithPath("data.createdAt").description("생성일")
                                        ).responseSchema(Schema.schema("게시글 응답")).build()
                        )
                )
        );

    }

        @Test
        @DisplayName("인증 실패")
        void givenPostRequest_whenCreatePost_thenReturn401() throws Exception {
            // Given
            given(postService.createPost(any(Authentication.class), any(PostRequestDto.class)))
                    .willThrow(new ApiException(UserResponseCode.AUTHORIZE_FAIL));
            // When
            mvc.perform(
                    RestDocumentationRequestBuilders.post("/api/posts")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request))
                            .accept(MediaType.APPLICATION_JSON)
                            .header("Authorization", "Bearer "+"accessToken")
            ).andExpect(status().is(401))

            .andDo(
                MockMvcRestDocumentationWrapper.document("게시글 생성 - 인증 실패",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        resource(
                                ResourceSnippetParameters.builder()
                                        .description("게시글 생성")
                                        .tag("Post").summary("게시글 생성 API")
                                        .requestFields(
                                                fieldWithPath("title").description("게시글 제목"),
                                                fieldWithPath("content").description("게시글 내용")
                                        ).requestHeaders(
                                                headerWithName("Authorization").description("Basic auth credentials")
                                        ).responseFields(
                                                fieldWithPath("code").description("응답 코드"),
                                                fieldWithPath("status").description("응답 상태"),
                                                fieldWithPath("message").description("응답 메시지"),
                                                fieldWithPath("data").description("응답 데이터")
                                        ).responseSchema(Schema.schema("게시글 응답")).build()
                        )
                )
            );

        }

        @Test
        @DisplayName("서버 오류")
        void givenPostRequest_whenCreatePost_thenReturn500() throws Exception {
            // Given
            given(postService.createPost(any(Authentication.class), any(PostRequestDto.class)))
                    .willThrow(new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error"));
            // When
            mvc.perform(
                    RestDocumentationRequestBuilders.post("/api/posts")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request))
                            .accept(MediaType.APPLICATION_JSON)
                            .header("Authorization", "Bearer "+ accessToken)
            ).andExpect(status().is(500))

            .andDo(
                MockMvcRestDocumentationWrapper.document("게시글 생성 - 서버 오류",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    resource(
                        ResourceSnippetParameters.builder()
                            .description("게시글 생성")
                            .tag("Post").summary("게시글 생성 API")
                            .requestFields(
                                fieldWithPath("title").description("게시글 제목"),
                                fieldWithPath("content").description("게시글 내용")
                            ).requestHeaders(
                                    headerWithName("Authorization").description("Basic auth credentials")
                            ).responseFields(
                                fieldWithPath("code").description("응답 코드"),
                                fieldWithPath("status").description("응답 상태"),
                                fieldWithPath("message").description("응답 메시지"),
                                fieldWithPath("data").description("응답 데이터")
                            ).responseSchema(Schema.schema("게시글 응답")).build()
                    )
                )
            );

        }
    }


    @Nested
    @DisplayName("게시글 수정 테스트")
    class UpdatePost {
        @Test
        @DisplayName("성공")
        void givenPostRequest_whenUpdatePost_thenReturn200() throws Exception {
            // Given
            given(postService.updatePost(anyLong(), any(PostRequestDto.class))).willReturn(postResponseDto);

            // When
            mvc.perform(
                    RestDocumentationRequestBuilders.patch("/api/posts/{postId}", postId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request))
                            .accept(MediaType.APPLICATION_JSON)
                            .header("Authorization", "Bearer "+accessToken)
            ).andExpect(status().isOk())

            .andDo(
                MockMvcRestDocumentationWrapper.document("게시글 수정 - 성공",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    resource(
                        ResourceSnippetParameters.builder()
                            .description("게시글 수정시")
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
                                    fieldWithPath("data.user").description("게시글 작성자 정보"),
                                    fieldWithPath("data.user.id").description("작성자 번호"),
                                    fieldWithPath("data.user.nickname").description("작성자 닉네임"),
                                    fieldWithPath("data.title").description("제목"),
                                    fieldWithPath("data.content").description("내용"),
                                    fieldWithPath("data.hit").description("추천 수"),
                                    fieldWithPath("data.view").description("좋아요 수"),
                                    fieldWithPath("data.createdAt").description("생성일")
                            ).responseSchema(Schema.schema("게시글 응답")).build()
                    )
                )
            );

        }

        @Test
        @DisplayName("인증 실패")
        void givenPostRequest_whenUpdatePost_thenReturn401() throws Exception {
            // Given
            given(postService.updatePost(anyLong(), any(PostRequestDto.class)))
                    .willThrow(new ApiException(UserResponseCode.AUTHORIZE_FAIL));
            // When
            mvc.perform(
                    RestDocumentationRequestBuilders.patch("/api/posts/{postId}", postId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request))
                            .accept(MediaType.APPLICATION_JSON)
                            .header("Authorization", "Bearer "+"accessToken")
            ).andExpect(status().is(401))

            .andDo(
                MockMvcRestDocumentationWrapper.document("게시글 수정 - 인증 실패",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    resource(
                        ResourceSnippetParameters.builder()
                            .description("게시글 수정시")
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
                                fieldWithPath("data").description("응답 데이터")
                            ).responseSchema(Schema.schema("게시글 응답")).build()
                    )
                )
            );

        }

        @Test
        @DisplayName("서버 오류")
        void givenPostRequest_whenUpdatePost_thenReturn500() throws Exception {
            // Given
            given(postService.updatePost(anyLong(), any(PostRequestDto.class)))
                    .willThrow(new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error"));
            // When
            mvc.perform(
                    RestDocumentationRequestBuilders.patch("/api/posts/{postId}", postId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request))
                            .accept(MediaType.APPLICATION_JSON)
                            .header("Authorization", "Bearer "+ accessToken)
            ).andExpect(status().is(500))

            .andDo(
                MockMvcRestDocumentationWrapper.document("게시글 수정 - 서버 오류",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    resource(
                        ResourceSnippetParameters.builder()
                            .description("게시글 수정시")
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
                                    fieldWithPath("data").description("응답 데이터")
                            ).responseSchema(Schema.schema("게시글 응답")).build()
                    )
                )
            );

        }

    }

    @Nested
    @DisplayName("게시글 단건 조회 테스트")
    class GetPost {
        @Test
        @DisplayName("성공")
        void givenPostRequest_whenGetPost_thenReturn200() throws Exception {
            // Given
            given(postService.getPost(anyLong())).willReturn(postResponseWithCommentDto);

            // When
            ResultActions actions = mvc.perform(
                    RestDocumentationRequestBuilders.get("/api/posts/{postId}", postId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request))
                            .accept(MediaType.APPLICATION_JSON)
                            .header("Authorization", "Bearer "+accessToken)
            ).andExpect(status().isOk());

            actions.andDo(
                    MockMvcRestDocumentationWrapper.document("게시글 단건 조회 - 성공",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            resource(
                                    ResourceSnippetParameters.builder()
                                            .description("게시글 단건 조회")
                                            .tag("Post").summary("게시글 단건 조회 API")
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
                                                    fieldWithPath("data.user").description("게시글 작성자 정보"),
                                                    fieldWithPath("data.user.id").description("작성자 번호"),
                                                    fieldWithPath("data.user.nickname").description("작성자 닉네임"),
                                                    fieldWithPath("data.title").description("제목"),
                                                    fieldWithPath("data.content").description("내용"),
                                                    fieldWithPath("data.hit").description("추천 수"),
                                                    fieldWithPath("data.view").description("좋아요 수"),
                                                    fieldWithPath("data.createdAt").description("생성일"),
                                                    fieldWithPath("data.comments").description("댓글"),
                                                    fieldWithPath("data.comments[].id").description("댓글 아이디"),
                                                    fieldWithPath("data.comments[].content").description("댓글 내용"),
                                                    fieldWithPath("data.comments[].writerId").description("작성자 아이디"),
                                                    fieldWithPath("data.comments[].writerNickname").description("작성자 닉네임"),
                                                    fieldWithPath("data.comments[].createdAt").description("생성일")
                                            ).responseSchema(Schema.schema("게시글 응답")).build()
                            )
                    )
            );

        }

        @Test
        @DisplayName("인증 실패")
        void givenPostRequest_whenGetPost_thenReturn401() throws Exception {
            // Given
            given(postService.getPost(anyLong()))
                    .willThrow(new ApiException(UserResponseCode.AUTHORIZE_FAIL));
            // When
            ResultActions actions = mvc.perform(
                    RestDocumentationRequestBuilders.get("/api/posts/{postId}", postId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request))
                            .accept(MediaType.APPLICATION_JSON)
                            .header("Authorization", "Bearer "+"accessToken")
            ).andExpect(status().is(401));

            actions.andDo(
                    MockMvcRestDocumentationWrapper.document("게시글 단건 조회 - 인증 실패",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            resource(
                                    ResourceSnippetParameters.builder()
                                            .description("게시글 단건 조회")
                                            .tag("Post").summary("게시글 단건 조회 API")
                                            .requestFields(
                                                    fieldWithPath("title").description("게시글 제목"),
                                                    fieldWithPath("content").description("게시글 내용")
                                            ).requestHeaders(
                                                    headerWithName("Authorization").description("Basic auth credentials")
                                            ).responseFields(
                                                    fieldWithPath("code").description("응답 코드"),
                                                    fieldWithPath("status").description("응답 상태"),
                                                    fieldWithPath("message").description("응답 메시지"),
                                                    fieldWithPath("data").description("응답 데이터")
                                            ).responseSchema(Schema.schema("게시글 응답")).build()
                            )
                    )
            );

        }

        @Test
        @DisplayName("서버 오류")
        void givenPostRequest_whenGetPost_thenReturn500() throws Exception {
            // Given
            given(postService.getPost(anyLong()))
                    .willThrow(new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error"));
            // When
            mvc.perform(
                    RestDocumentationRequestBuilders.get("/api/posts/{postId}", postId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request))
                            .accept(MediaType.APPLICATION_JSON)
                            .header("Authorization", "Bearer "+ accessToken)
            ).andExpect(status().is(500))

            .andDo(
                MockMvcRestDocumentationWrapper.document("게시글 단건 조회 - 서버 오류",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    resource(
                        ResourceSnippetParameters.builder()
                            .description("게시글 단건 조회")
                            .tag("Post").summary("게시글 단건 조회 API")
                            .requestFields(
                                    fieldWithPath("title").description("게시글 제목"),
                                    fieldWithPath("content").description("게시글 내용")
                            ).requestHeaders(
                                    headerWithName("Authorization").description("Basic auth credentials")
                            ).responseFields(
                                    fieldWithPath("code").description("응답 코드"),
                                    fieldWithPath("status").description("응답 상태"),
                                    fieldWithPath("message").description("응답 메시지"),
                                    fieldWithPath("data").description("응답 데이터")
                            ).responseSchema(Schema.schema("게시글 응답")).build()
                    )
                )
            );

        }
    }

    @Nested
    @DisplayName("게시글 리스트 조회")
    class GetPostList {
        @Test
        @DisplayName("성공")
        void givenPostRequest_whenGetPostList_thenReturn200() throws Exception {
            // Given
            PageInfoWithPosts pageInfoWithPosts = new PageInfoWithPosts(0,1, List.of(postResponseDto));
            given(postService.getPosts(any(Pageable.class))).willReturn(pageInfoWithPosts);

            // When
            mvc.perform(
                    RestDocumentationRequestBuilders.get("/api/posts")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request))
                            .accept(MediaType.APPLICATION_JSON)
                            .header("Authorization", "Bearer "+accessToken)
            ).andExpect(status().isOk())

            .andDo(
                    MockMvcRestDocumentationWrapper.document("게시글 리스트 조회 - 성공",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            resource(
                                    ResourceSnippetParameters.builder()
                                            .description("게시글 리스트 조회")
                                            .tag("Post").summary("게시글 리스트 조회 API")
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
                                                    fieldWithPath("data.pageNumber").description("현재 페이지 번호"),
                                                    fieldWithPath("data.totalPages").description("총 페이지 수"),
                                                    fieldWithPath("data.posts").description("게시글 리스트"),
                                                    fieldWithPath("data.posts[].id").description("게시글 번호"),
                                                    fieldWithPath("data.posts[].user").description("게시글 작성자 정보"),
                                                    fieldWithPath("data.posts[].user.id").description("작성자 번호"),
                                                    fieldWithPath("data.posts[].user.nickname").description("작성자 닉네임"),
                                                    fieldWithPath("data.posts[].title").description("제목"),
                                                    fieldWithPath("data.posts[].content").description("내용"),
                                                    fieldWithPath("data.posts[].hit").description("추천 수"),
                                                    fieldWithPath("data.posts[].view").description("좋아요 수"),
                                                    fieldWithPath("data.posts[].createdAt").description("생성일")
                                            ).responseSchema(Schema.schema("게시글 응답")).build()
                            )
                    )
            );

        }

        @Test
        @DisplayName("인증 실패")
        void givenPostRequest_whenGetPostList_thenReturn401() throws Exception {
            // Given
            given(postService.getPosts(any(Pageable.class)))
                    .willThrow(new ApiException(UserResponseCode.AUTHORIZE_FAIL));
            // When
            ResultActions actions = mvc.perform(
                    RestDocumentationRequestBuilders.get("/api/posts")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request))
                            .accept(MediaType.APPLICATION_JSON)
                            .header("Authorization", "Bearer "+"accessToken")
            ).andExpect(status().is(401));

            actions.andDo(
                    MockMvcRestDocumentationWrapper.document("게시글 리스트 조회 - 인증 실패",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            resource(
                                    ResourceSnippetParameters.builder()
                                            .description("게시글 리스트 조회")
                                            .tag("Post").summary("게시글 리스트 조회 API")
                                            .requestFields(
                                                    fieldWithPath("title").description("게시글 제목"),
                                                    fieldWithPath("content").description("게시글 내용")
                                            ).requestHeaders(
                                                    headerWithName("Authorization").description("Basic auth credentials")
                                            ).responseFields(
                                                    fieldWithPath("code").description("응답 코드"),
                                                    fieldWithPath("status").description("응답 상태"),
                                                    fieldWithPath("message").description("응답 메시지"),
                                                    fieldWithPath("data").description("응답 데이터")
                                            ).responseSchema(Schema.schema("게시글 응답")).build()
                            )
                    )
            );

        }

        @Test
        @DisplayName("서버 오류")
        void givenPostRequest_whenGetPostList_thenReturn500() throws Exception {
            // Given
            given(postService.getPosts(any(Pageable.class)))
                    .willThrow(new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error"));
            // When
            ResultActions actions = mvc.perform(
                    RestDocumentationRequestBuilders.get("/api/posts")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request))
                            .accept(MediaType.APPLICATION_JSON)
                            .header("Authorization", "Bearer "+ accessToken)
            ).andExpect(status().is(500));

            actions.andDo(
                    MockMvcRestDocumentationWrapper.document("게시글 리스트 조회 - 서버 오류",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            resource(
                                    ResourceSnippetParameters.builder()
                                            .description("게시글 리스트 조회")
                                            .tag("Post").summary("게시글 리스트 조회 API")
                                            .requestFields(
                                                    fieldWithPath("title").description("게시글 제목"),
                                                    fieldWithPath("content").description("게시글 내용")
                                            ).requestHeaders(
                                                    headerWithName("Authorization").description("Basic auth credentials")
                                            ).responseFields(
                                                    fieldWithPath("code").description("응답 코드"),
                                                    fieldWithPath("status").description("응답 상태"),
                                                    fieldWithPath("message").description("응답 메시지"),
                                                    fieldWithPath("data").description("응답 데이터")
                                            ).responseSchema(Schema.schema("게시글 응답")).build()
                            )
                    )
            );

        }
    }




}