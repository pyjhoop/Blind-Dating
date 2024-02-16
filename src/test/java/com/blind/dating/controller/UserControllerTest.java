package com.blind.dating.controller;

import com.blind.dating.config.SecurityConfig;
import com.blind.dating.domain.interest.Interest;
import com.blind.dating.domain.user.Role;
import com.blind.dating.domain.user.UserAccount;
import com.blind.dating.domain.user.UserController;
import com.blind.dating.dto.interest.InterestDto;
import com.blind.dating.dto.question.QuestionDto;
import com.blind.dating.dto.user.UserInfoDto;
import com.blind.dating.dto.user.UserUpdateRequestDto;
import com.blind.dating.dto.user.UserWithInterestAndQuestionDto;
import com.blind.dating.domain.user.UserAccountRedisRepository;
import com.blind.dating.security.TokenProvider;
import com.blind.dating.domain.user.UserService;
import com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper;
import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.epages.restdocs.apispec.Schema;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

import static com.epages.restdocs.apispec.ResourceDocumentation.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("UserController -테스트")
@WebMvcTest(UserController.class)
@Import(SecurityConfig.class)
class UserControllerTest extends ControllerTestConfig{

    @Autowired
    private MockMvc mvc;
    @Autowired
    private ObjectMapper obj;


    @MockBean private UserService userService;
    @MockBean private TokenProvider tokenProvider;
    @MockBean private UserAccountRedisRepository userAccountRedisRepository;

    private UserAccount user;
    private Authentication authentication;
    List<InterestDto> interests;
    private UserInfoDto dto;

    @BeforeEach
    void setUp(){
        user = new UserAccount(1L,"userId","userPassword","nickname","서울","INTP","M",false, "안녕", LocalDateTime.now(), Role.USER.getValue(),null,null);
        user.setInterests(List.of(new Interest()));
        authentication = new UsernamePasswordAuthenticationToken("1",user.getUserPassword());
        interests = List.of(InterestDto.of(1L,"자전거 타기"),
                InterestDto.of(2L, "놀기"), InterestDto.of(3L,"게임하기"));
        dto = new UserInfoDto(1L, "nickname1","서울","INTP","M", interests, "하이요");

    }

    @Nested
    @DisplayName("전체 유저 조회")
    class MaleAndFemaleUser {
        @DisplayName("조회 성공")
        @Test
        @WithMockUser(username = "1", password = "", roles = "USER")
        void testGetMaileAndFemaleUsersThenReturn200() throws Exception {
            // Given
            Pageable pageable = PageRequest.of(0, 1);
            Page<UserInfoDto> page = new PageImpl<>(List.of(dto), pageable,1L);

            given(userService.getMaleAndFemaleUsers(any(Pageable.class), any(Authentication.class))).willReturn(page);

            ResultActions actions = mvc.perform(
                    RestDocumentationRequestBuilders.get("/api/users/all")
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON)
                            .header("Authorization", "Bearer "+"accessToken")
            ).andDo(
                    MockMvcRestDocumentationWrapper.document("전체 유저 조회 - 성공",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            resource(
                                    ResourceSnippetParameters.builder()
                                            .description("남성과 여성을 조회한다.")
                                            .tag("User").summary("남성과 여성 추천 유저 조회 API")
                                            .requestFields()
                                            .requestHeaders(
                                                    headerWithName("Authorization").description("Basic auth credentials")
                                            )
                                            .responseFields(
                                                    fieldWithPath("code").description("응답 코드"),
                                                    fieldWithPath("status").description("응답 상태"),
                                                    fieldWithPath("message").description("응답 메시지"),
                                                    fieldWithPath("data").description("응답 데이터"),
                                                    fieldWithPath("data.pageNumber").description("페이지 번호"),
                                                    fieldWithPath("data.totalPages").description("총 페이지 수"),
                                                    fieldWithPath("data.content").description("유저정보"),
                                                    fieldWithPath("data.content[].id").description("유저 유니크 아이디"),
                                                    fieldWithPath("data.content[].nickname").description("닉네임"),
                                                    fieldWithPath("data.content[].region").description("사는 지역"),
                                                    fieldWithPath("data.content[].mbti").description("MBTI"),
                                                    fieldWithPath("data.content[].gender").description("성별"),
                                                    fieldWithPath("data.content[].interests").description("관심사 리스트"),
                                                    fieldWithPath("data.content[].interests[].id").description("관심사 아이디"),
                                                    fieldWithPath("data.content[].interests[].interestName").description("관심사 명"),
                                                    fieldWithPath("data.content[].questions").description("질의 답변 리스트"),
                                                    fieldWithPath("data.content[].questions[].id").description("질의 답변 아이디"),
                                                    fieldWithPath("data.content[].questions[].status").description("질의 답변 상태"),
                                                    fieldWithPath("data.content[].selfIntroduction").description("자기소개")
                                            ).responseSchema(Schema.schema("유저 리스트 조회")).build()
                            )
                    )
            );
            actions.andExpect(status().isOk());
        }

        @DisplayName("인증 실패")
        @Test
        void testGetMaileAndFemaleUsersThenReturn401() throws Exception {
            // Given
            Pageable pageable = PageRequest.of(0, 1);
            Page<UserInfoDto> page = new PageImpl<>(List.of(dto), pageable,1L);

            given(userService.getMaleAndFemaleUsers(any(Pageable.class), any(Authentication.class))).willReturn(page);

            ResultActions actions = mvc.perform(
                    RestDocumentationRequestBuilders.get("/api/users/all")
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON)
                            .header("Authorization", "Bearer "+"accessToken")
            ).andDo(
                    MockMvcRestDocumentationWrapper.document("전체 유저 조회 - 인증 실패",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            resource(
                                    ResourceSnippetParameters.builder()
                                            .description("유저 조회시 인증 실패")
                                            .tag("User").summary("남성과 여성 추천 유저 조회 API")
                                            .requestFields()
                                            .requestHeaders(
                                                    headerWithName("Authorization").description("Basic auth credentials")
                                            )
                                            .responseFields(
                                                    fieldWithPath("code").description("응답 코드"),
                                                    fieldWithPath("status").description("응답 상태"),
                                                    fieldWithPath("message").description("응답 메시지"),
                                                    fieldWithPath("data").description("응답 데이터")
                                            ).responseSchema(Schema.schema("유저 리스트 조회 실패")).build()
                            )
                    )
            );
            actions.andExpect(status().is(401));
        }

        @DisplayName("서버 에러")
        @Test
        @WithMockUser(username = "1", password = "", roles = "USER")
        void testGetMaileAndFemaleUsersThenReturn500() throws Exception {
            // Given
            Pageable pageable = PageRequest.of(0, 1);
            Page<UserInfoDto> page = new PageImpl<>(List.of(dto), pageable,1L);


            given(userService.getMaleAndFemaleUsers(any(Pageable.class), any(Authentication.class)))
                    .willThrow(new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error"));

            ResultActions actions = mvc.perform(
                    RestDocumentationRequestBuilders.get("/api/users/all")
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON)
                            .header("Authorization", "Bearer "+"accessToken")
            ).andDo(
                    MockMvcRestDocumentationWrapper.document("전체 유저 조회 - 서버 에러",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            resource(
                                    ResourceSnippetParameters.builder()
                                            .description("추천 유저 조회시 서버 에러")
                                            .tag("User").summary("남성과 여성 추천 유저 조회 API")
                                            .requestFields()
                                            .requestHeaders(
                                                    headerWithName("Authorization").description("Basic auth credentials")
                                            )
                                            .responseFields(
                                                    fieldWithPath("code").description("응답 코드"),
                                                    fieldWithPath("status").description("응답 상태"),
                                                    fieldWithPath("message").description("응답 메시지"),
                                                    fieldWithPath("data").description("응답 데이터")
                                            ).responseSchema(Schema.schema("유저 리스트 조회 실패")).build()
                            )
                    )
            );
            actions.andExpect(status().is(500));
        }
    }

    @Nested
    @DisplayName("남성 유저 조회")
    class MaleUser {
        @DisplayName("조회 성공")
        @Test
        @WithMockUser(username = "1", password = "", roles = "USER")
        void testGetMaileUsersThenReturn200() throws Exception {
            // Given
            Pageable pageable = PageRequest.of(0, 1);
            Page<UserInfoDto> page = new PageImpl<>(List.of(dto), pageable,1L);

            given(userService.getMaleUsers(any(Pageable.class), any(Authentication.class))).willReturn(page);


            ResultActions actions = mvc.perform(
                    RestDocumentationRequestBuilders.get("/api/users/male")
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON)
                            .header("Authorization", "Bearer "+"accessToken")
            ).andDo(
                    MockMvcRestDocumentationWrapper.document("남성 유저 조회 - 성공",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            resource(
                                    ResourceSnippetParameters.builder()
                                            .description("남성을 최근 로그인 한 순으로 조회한다.")
                                            .tag("User").summary("남성 유저조회 API")
                                            .requestFields()
                                            .requestHeaders(
                                                    headerWithName("Authorization").description("Basic auth credentials")
                                            )
                                            .responseFields(
                                                    fieldWithPath("code").description("응답 코드"),
                                                    fieldWithPath("status").description("응답 상태"),
                                                    fieldWithPath("message").description("응답 메시지"),
                                                    fieldWithPath("data").description("응답 데이터"),
                                                    fieldWithPath("data.pageNumber").description("페이지 번호"),
                                                    fieldWithPath("data.totalPages").description("총 페이지 수"),
                                                    fieldWithPath("data.content").description("유저정보"),
                                                    fieldWithPath("data.content[].id").description("유저 유니크 아이디"),
                                                    fieldWithPath("data.content[].nickname").description("닉네임"),
                                                    fieldWithPath("data.content[].region").description("사는 지역"),
                                                    fieldWithPath("data.content[].mbti").description("MBTI"),
                                                    fieldWithPath("data.content[].gender").description("성별"),
                                                    fieldWithPath("data.content[].interests").description("관심사 리스트"),
                                                    fieldWithPath("data.content[].interests[].id").description("관심사 아이디"),
                                                    fieldWithPath("data.content[].interests[].interestName").description("관심사 명"),
                                                    fieldWithPath("data.content[].questions").description("질의 답변 리스트"),
                                                    fieldWithPath("data.content[].questions[].id").description("질의 답변 아이디"),
                                                    fieldWithPath("data.content[].questions[].status").description("질의 답변 상태"),
                                                    fieldWithPath("data.content[].selfIntroduction").description("자기소개")
                                            ).responseSchema(Schema.schema("유저 리스트 조회")).build()
                            )
                    )
            );
            actions.andExpect(status().isOk());
        }

        @DisplayName("인증 실패")
        @Test
        void testGetMaileUsersThenReturn401() throws Exception {
            // Given
            Pageable pageable = PageRequest.of(0, 1);
            Page<UserInfoDto> page = new PageImpl<>(List.of(dto), pageable,1L);

            given(userService.getMaleUsers(any(Pageable.class), any(Authentication.class))).willReturn(page);


            ResultActions actions = mvc.perform(
                    RestDocumentationRequestBuilders.get("/api/users/male")
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON)
                            .header("Authorization", "Bearer "+"accessToken")
            ).andDo(
                    MockMvcRestDocumentationWrapper.document("남성 유저 조회 - 인증 실패",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            resource(
                                    ResourceSnippetParameters.builder()
                                            .description("남성을 최근 로그인 한 순으로 조회한다.")
                                            .tag("User").summary("남성 유저조회 API")
                                            .requestFields()
                                            .requestHeaders(
                                                    headerWithName("Authorization").description("Basic auth credentials")
                                            )
                                            .responseFields(
                                                    fieldWithPath("code").description("응답 코드"),
                                                    fieldWithPath("status").description("응답 상태"),
                                                    fieldWithPath("message").description("응답 메시지"),
                                                    fieldWithPath("data").description("응답 데이터")
                                            ).responseSchema(Schema.schema("유저 리스트 조회 실패")).build()
                            )
                    )
            );
            actions.andExpect(status().is(401));
        }

        @DisplayName("서버 에러")
        @Test
        @WithMockUser(username = "1", password = "", roles = "USER")
        void testGetMaileUsersThenReturn500() throws Exception {
            // Given
            Pageable pageable = PageRequest.of(0, 1);
            Page<UserInfoDto> page = new PageImpl<>(List.of(dto), pageable,1L);

            given(userService.getMaleUsers(any(Pageable.class), any(Authentication.class)))
                    .willThrow(new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error"));

            ResultActions actions = mvc.perform(
                    RestDocumentationRequestBuilders.get("/api/users/male")
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON)
                            .header("Authorization", "Bearer "+"accessToken")
            ).andDo(
                    MockMvcRestDocumentationWrapper.document("남성 유저 조회 - 서버 에러",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            resource(
                                    ResourceSnippetParameters.builder()
                                            .description("남성을 최근 로그인 한 순으로 조회한다.")
                                            .tag("User").summary("남성 유저조회 API")
                                            .requestFields()
                                            .requestHeaders(
                                                    headerWithName("Authorization").description("Basic auth credentials")
                                            )
                                            .responseFields(
                                                    fieldWithPath("code").description("응답 코드"),
                                                    fieldWithPath("status").description("응답 상태"),
                                                    fieldWithPath("message").description("응답 메시지"),
                                                    fieldWithPath("data").description("응답 데이터")
                                            ).responseSchema(Schema.schema("유저 리스트 조회 실패")).build()
                            )
                    )
            );
            actions.andExpect(status().is(500));
        }


    }

    @Nested
    @DisplayName("여성 유저 조회")
    class FemaleUser {
        @DisplayName("조회 성공")
        @Test
        @WithMockUser(username = "1", password = "", roles = "USER")
        void testGetFemaleUsersThen200() throws Exception {
            // Given
            Pageable pageable = PageRequest.of(0, 1);
            dto.setGender("W");
            Page<UserInfoDto> page = new PageImpl<>(List.of(dto), pageable,1L);

            given(userService.getFemaleUsers(any(Pageable.class), any(Authentication.class))).willReturn(page);

            ResultActions actions = mvc.perform(
                    RestDocumentationRequestBuilders.get("/api/users/female")
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON)
                            .header("Authorization", "Bearer "+"accessToken")
            ).andDo(
                    MockMvcRestDocumentationWrapper.document("여성 유저 조회 - 성공",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            resource(
                                    ResourceSnippetParameters.builder()
                                            .description("여성을 최근 로그인 한 순으로 조회한다.")
                                            .tag("User").summary("여성 유저조회 API")
                                            .requestFields()
                                            .requestHeaders(
                                                    headerWithName("Authorization").description("Basic auth credentials")
                                            )
                                            .responseFields(
                                                    fieldWithPath("code").description("응답 코드"),
                                                    fieldWithPath("status").description("응답 상태"),
                                                    fieldWithPath("message").description("응답 메시지"),
                                                    fieldWithPath("data").description("응답 데이터"),
                                                    fieldWithPath("data.pageNumber").description("페이지 번호"),
                                                    fieldWithPath("data.totalPages").description("총 페이지 수"),
                                                    fieldWithPath("data.content").description("유저정보"),
                                                    fieldWithPath("data.content[].id").description("유저 유니크 아이디"),
                                                    fieldWithPath("data.content[].nickname").description("닉네임"),
                                                    fieldWithPath("data.content[].region").description("사는 지역"),
                                                    fieldWithPath("data.content[].mbti").description("MBTI"),
                                                    fieldWithPath("data.content[].gender").description("성별"),
                                                    fieldWithPath("data.content[].interests").description("관심사 리스트"),
                                                    fieldWithPath("data.content[].interests[].id").description("관심사 아이디"),
                                                    fieldWithPath("data.content[].interests[].interestName").description("관심사 명"),
                                                    fieldWithPath("data.content[].questions").description("질의 답변 리스트"),
                                                    fieldWithPath("data.content[].questions[].id").description("질의 답변 아이디"),
                                                    fieldWithPath("data.content[].questions[].status").description("질의 답변 상태"),
                                                    fieldWithPath("data.content[].selfIntroduction").description("자기소개")
                                            ).responseSchema(Schema.schema("유저 리스트 조회")).build()
                            )
                    )
            );
            actions.andExpect(status().isOk());
        }

        @DisplayName("인증 실패")
        @Test
        void testGetFemaleUsersThen401() throws Exception {
            // Given
            dto.setGender("W");
            Pageable pageable = PageRequest.of(0, 1);
            Page<UserInfoDto> page = new PageImpl<>(List.of(dto), pageable,1L);

            given(userService.getFemaleUsers(any(Pageable.class), any(Authentication.class))).willReturn(page);

            ResultActions actions = mvc.perform(
                    RestDocumentationRequestBuilders.get("/api/users/female")
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON)
                            .header("Authorization", "Bearer "+"accessToken")
            ).andDo(
                    MockMvcRestDocumentationWrapper.document("여성 유저 조회 - 인증 실패",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            resource(
                                    ResourceSnippetParameters.builder()
                                            .description("여성을 최근 로그인 한 순으로 조회한다.")
                                            .tag("User").summary("여성 유저조회 API")
                                            .requestFields()
                                            .requestHeaders(
                                                    headerWithName("Authorization").description("Basic auth credentials")
                                            )
                                            .responseFields(
                                                    fieldWithPath("code").description("응답 코드"),
                                                    fieldWithPath("status").description("응답 상태"),
                                                    fieldWithPath("message").description("응답 메시지"),
                                                    fieldWithPath("data").description("응답 데이터")
                                            ).responseSchema(Schema.schema("유저 리스트 조회 실패")).build()
                            )
                    )
            );
            actions.andExpect(status().is(401));
        }
        @DisplayName("서버 에러")
        @Test
        @WithMockUser(username = "1", password = "", roles = "USER")
        void testGetFemaleUsersThenReturn500() throws Exception {
            // Given
            Pageable pageable = PageRequest.of(0, 1);
            Page<UserInfoDto> page = new PageImpl<>(List.of(dto), pageable,1L);

            given(userService.getFemaleUsers(any(Pageable.class), any(Authentication.class)))
                    .willThrow(new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error"));

            ResultActions actions = mvc.perform(
                    RestDocumentationRequestBuilders.get("/api/users/female")
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON)
                            .header("Authorization", "Bearer "+"accessToken")
            ).andDo(
                    MockMvcRestDocumentationWrapper.document("여성 유저 조회 - 서버 에러",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            resource(
                                    ResourceSnippetParameters.builder()
                                            .description("여성을 최근 로그인 한 순으로 조회한다.")
                                            .tag("User").summary("여성 유저조회 API")
                                            .requestFields()
                                            .requestHeaders(
                                                    headerWithName("Authorization").description("Basic auth credentials")
                                            )
                                            .responseFields(
                                                    fieldWithPath("code").description("응답 코드"),
                                                    fieldWithPath("status").description("응답 상태"),
                                                    fieldWithPath("message").description("응답 메시지"),
                                                    fieldWithPath("data").description("응답 데이터")
                                            ).responseSchema(Schema.schema("유저 리스트 조회 실패")).build()
                            )
                    )
            );
            actions.andExpect(status().is(500));
        }
    }


    

    @Nested
    @DisplayName("내정보 조회")
    class GetMyInfo{
        @DisplayName("성공")
        @Test
        @WithMockUser(username = "1", password = "", roles = "USER")
        public void testGetMyInfoThen200() throws Exception {
            //Given
            UserWithInterestAndQuestionDto dto1 = UserWithInterestAndQuestionDto.of(1L,"userId1","nickname1","서울","INTP","M",interests,null);
            given(userService.getMyInfo(any(Authentication.class))).willReturn(dto1);

            //When && Then
            ResultActions actions = mvc.perform(
                    RestDocumentationRequestBuilders.get("/api/users")
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON)
                            .header("Authorization", "Bearer "+"accessToken")
            ).andDo(
                    MockMvcRestDocumentationWrapper.document("내정보 조회 - 성공",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            resource(
                                    ResourceSnippetParameters.builder()
                                            .description("내정보 조회를 조회합니다.")
                                            .tag("User").description("내정보 조회 API")
                                            .requestFields()
                                            .requestHeaders(
                                                    headerWithName("Authorization").description("Basic auth credentials")
                                            )
                                            .responseFields(
                                                    fieldWithPath("code").description("응답 코드"),
                                                    fieldWithPath("status").description("응답 상태"),
                                                    fieldWithPath("message").description("응답 메시지"),
                                                    fieldWithPath("data").description("응답 데이터"),
                                                    fieldWithPath("data.id").description("유저 유니크 번호"),
                                                    fieldWithPath("data.userId").description("유저 아이디"),
                                                    fieldWithPath("data.nickname").description("닉네임"),
                                                    fieldWithPath("data.region").description("사는 지역"),
                                                    fieldWithPath("data.mbti").description("MBTI"),
                                                    fieldWithPath("data.gender").description("성별"),
                                                    fieldWithPath("data.interests").description("관심사 리스트"),
                                                    fieldWithPath("data.interests[].id").description("관심사 아이디"),
                                                    fieldWithPath("data.interests[].interestName").description("관심사 명"),
                                                    fieldWithPath("data.questions").description("질의 답변 리스트"),
                                                    fieldWithPath("data.questions[].id").description("질의 답변 아이디"),
                                                    fieldWithPath("data.questions[].status").description("질의 답변 상태"),
                                                    fieldWithPath("data.selfIntroduction").description("자기소개")
                                            ).responseSchema(Schema.schema("내정보 조회 성공")).build()
                            )
                    )
            );
            actions.andExpect(status().isOk());
        }

        @DisplayName("인증 실패")
        @Test
        public void testGetMyInfoThen401() throws Exception {
            //Given
            UserWithInterestAndQuestionDto dto1 = UserWithInterestAndQuestionDto.of(1L,"userId1","nickname1","서울","INTP","M",interests,"안녕");
            given(userService.getMyInfo(any(Authentication.class))).willReturn(dto1);

            //When && Then
            ResultActions actions = mvc.perform(
                    RestDocumentationRequestBuilders.get("/api/users")
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON)
                            .header("Authorization", "Bearer "+"accessToken")
            ).andDo(
                    MockMvcRestDocumentationWrapper.document("내정보 조회 - 인증 실패",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            resource(
                                    ResourceSnippetParameters.builder()
                                            .description("내정보 조회를 조회합니다.")
                                            .tag("User").description("내정보 조회 API")
                                            .requestFields()
                                            .requestHeaders(
                                                    headerWithName("Authorization").description("Basic auth credentials")
                                            )
                                            .responseFields(
                                                    fieldWithPath("code").description("응답 코드"),
                                                    fieldWithPath("status").description("응답 상태"),
                                                    fieldWithPath("message").description("응답 메시지"),
                                                    fieldWithPath("data").description("응답 데이터")
                                            ).responseSchema(Schema.schema("내정보 조회 실패")).build()
                            )
                    )
            );
            actions.andExpect(status().is(401));
        }

        @DisplayName("서버 에러")
        @Test
        @WithMockUser(username = "1", password = "", roles = "USER")
        public void testGetMyInfoThen500() throws Exception {
            //Given
            UserWithInterestAndQuestionDto dto1 = UserWithInterestAndQuestionDto.of(1L,"userId1","nickname1","서울","INTP","M",interests,"안녕");
            given(userService.getMyInfo(any(Authentication.class))).willReturn(dto1);
            given(userService.getMyInfo(any(Authentication.class)))
                    .willThrow(new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error"));


            //When && Then
            ResultActions actions = mvc.perform(
                    RestDocumentationRequestBuilders.get("/api/users")
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON)
                            .header("Authorization", "Bearer "+"accessToken")
            ).andDo(
                    MockMvcRestDocumentationWrapper.document("내정보 조회 - 서버 에러",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            resource(
                                    ResourceSnippetParameters.builder()
                                            .description("내정보 조회를 조회합니다.")
                                            .tag("User").description("내정보 조회 API")
                                            .requestFields()
                                            .requestHeaders(
                                                    headerWithName("Authorization").description("Basic auth credentials")
                                            )
                                            .responseFields(
                                                    fieldWithPath("code").description("응답 코드"),
                                                    fieldWithPath("status").description("응답 상태"),
                                                    fieldWithPath("message").description("응답 메시지"),
                                                    fieldWithPath("data").description("응답 데이터")
                                            ).responseSchema(Schema.schema("내정보 조회 실패")).build()
                            )
                    )
            );
            actions.andExpect(status().is(500));
        }

    }



    @Nested
    @DisplayName("내정보 수정")
    class UpdateMyInfo{
        @DisplayName("성공")
        @Test
        @WithMockUser(username = "1", password = "", roles = "USER")
        public void testUpdateMyInfoThen200() throws Exception {
            List<String> list = List.of("놀기","게임하기");
            UserUpdateRequestDto dto = new UserUpdateRequestDto("인천","ESTP",list,"안녕하세요");
            UserWithInterestAndQuestionDto dto1 = UserWithInterestAndQuestionDto.of(1L,"userId1","nickname1","서울","INTP","M",interests,"안녕");

            List<Interest> list1 = List.of(new Interest(1L, user, "놀기"), new Interest(2L, user, "게임하기"));
            user.setInterests(list1);
            given(userService.updateMyInfo(any(Authentication.class), eq(dto))).willReturn(user);

            //When && Then
            ResultActions actions = mvc.perform(
                    RestDocumentationRequestBuilders.put("/api/users")
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON)
                            .header("Authorization", "Bearer "+"accessToken")
                            .content(objectMapper.writeValueAsString(dto))
            ).andDo(
                    MockMvcRestDocumentationWrapper.document("내정보 수정 - 성공",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            resource(
                                    ResourceSnippetParameters.builder()
                                            .description("내정보 수정 API")
                                            .tag("User").description("내정보 수정 API")
                                            .requestHeaders(
                                                    headerWithName("Authorization").description("Basic auth credentials")
                                            )
                                            .requestFields(
                                                    fieldWithPath("region").description("사는 지역"),
                                                    fieldWithPath("mbti").description("MBTI"),
                                                    fieldWithPath("interests").description("관심사"),
                                                    fieldWithPath("selfIntroduction").description("자기소개")
                                            ).requestSchema(Schema.schema("내정보 수정 요청"))
                                            .responseFields(
                                                    fieldWithPath("code").description("응답 코드"),
                                                    fieldWithPath("status").description("응답 상태"),
                                                    fieldWithPath("message").description("응답 메시지"),
                                                    fieldWithPath("data").description("응답 데이터"),
                                                    fieldWithPath("data.id").description("유저 유니크 번호"),
                                                    fieldWithPath("data.userId").description("유저 아이디"),
                                                    fieldWithPath("data.nickname").description("닉네임"),
                                                    fieldWithPath("data.region").description("사는 지역"),
                                                    fieldWithPath("data.mbti").description("MBTI"),
                                                    fieldWithPath("data.gender").description("성별"),
                                                    fieldWithPath("data.interests").description("관심사 리스트"),
                                                    fieldWithPath("data.interests[].id").description("관심사 아이디"),
                                                    fieldWithPath("data.interests[].interestName").description("관심사 명"),
                                                    fieldWithPath("data.questions").description("질의 답변 리스트"),
                                                    fieldWithPath("data.questions[].id").description("질의 답변 아이디"),
                                                    fieldWithPath("data.questions[].status").description("질의 답변 상태"),
                                                    fieldWithPath("data.selfIntroduction").description("자기소개")
                                            ).responseSchema(Schema.schema("내정보 수정 성공")).build()
                            )
                    )
            );
            actions.andExpect(status().is(200));
        }

        @DisplayName("인증 실패")
        @Test
        public void testUpdateMyInfoThen401() throws Exception {
            List<String> list = List.of("놀기","게임하기");
            UserUpdateRequestDto dto = new UserUpdateRequestDto("인천","ESTP",list,"안녕하세요");
            UserWithInterestAndQuestionDto dto1 = UserWithInterestAndQuestionDto.of(1L,"userId1","nickname1","서울","INTP","M",interests,"안녕");

            List<Interest> list1 = List.of(new Interest(1L, user, "놀기"), new Interest(2L, user, "게임하기"));
            user.setInterests(list1);
            given(userService.updateMyInfo(any(Authentication.class), eq(dto))).willReturn(user);

            //When && Then
            ResultActions actions = mvc.perform(
                    RestDocumentationRequestBuilders.put("/api/users")
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON)
                            .header("Authorization", "Bearer "+"accessToken")
                            .content(objectMapper.writeValueAsString(dto))
            ).andDo(
                    MockMvcRestDocumentationWrapper.document("내정보 수정 - 인증 실패",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            resource(
                                    ResourceSnippetParameters.builder()
                                            .description("내정보 수정 API")
                                            .tag("User").description("내정보 수정 API")
                                            .requestHeaders(
                                                    headerWithName("Authorization").description("Basic auth credentials")
                                            )
                                            .requestFields(
                                                    fieldWithPath("region").description("사는 지역"),
                                                    fieldWithPath("mbti").description("MBTI"),
                                                    fieldWithPath("interests").description("관심사"),
                                                    fieldWithPath("selfIntroduction").description("자기소개")
                                            ).requestSchema(Schema.schema("내정보 수정 요청"))
                                            .responseFields(
                                                    fieldWithPath("code").description("응답 코드"),
                                                    fieldWithPath("status").description("응답 상태"),
                                                    fieldWithPath("message").description("응답 메시지"),
                                                    fieldWithPath("data").description("응답 데이터")
                                            ).responseSchema(Schema.schema("내정보 수정 실패")).build()
                            )
                    )
            );
            actions.andExpect(status().is(401));
        }

        @DisplayName("서버 오류")
        @Test
        @WithMockUser(username = "1", password = "", roles = "USER")
        public void testUpdateMyInfoThen500() throws Exception {
            List<String> list = List.of("놀기","게임하기");
            UserUpdateRequestDto dto = new UserUpdateRequestDto("인천","ESTP",list,"안녕하세요");
            UserWithInterestAndQuestionDto dto1 = UserWithInterestAndQuestionDto.of(1L,"userId1","nickname1","서울","INTP","M",interests,"안녕");

            List<Interest> list1 = List.of(new Interest(1L, user, "놀기"), new Interest(2L, user, "게임하기"));
            user.setInterests(list1);
            given(userService.updateMyInfo(any(Authentication.class), eq(dto)))
                    .willThrow(new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error"));

            //When && Then
            ResultActions actions = mvc.perform(
                    RestDocumentationRequestBuilders.put("/api/users")
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON)
                            .header("Authorization", "Bearer "+"accessToken")
                            .content(objectMapper.writeValueAsString(dto))
            ).andDo(
                    MockMvcRestDocumentationWrapper.document("내정보 수정 - 서버 오류",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            resource(
                                    ResourceSnippetParameters.builder()
                                            .description("내정보 수정 API")
                                            .tag("User").description("내정보 수정 API")
                                            .requestHeaders(
                                                    headerWithName("Authorization").description("Basic auth credentials")
                                            )
                                            .requestFields(
                                                    fieldWithPath("region").description("사는 지역"),
                                                    fieldWithPath("mbti").description("MBTI"),
                                                    fieldWithPath("interests").description("관심사"),
                                                    fieldWithPath("selfIntroduction").description("자기소개")
                                            ).requestSchema(Schema.schema("내정보 수정 요청"))
                                            .responseFields(
                                                    fieldWithPath("code").description("응답 코드"),
                                                    fieldWithPath("status").description("응답 상태"),
                                                    fieldWithPath("message").description("응답 메시지"),
                                                    fieldWithPath("data").description("응답 데이터")
                                            ).responseSchema(Schema.schema("내정보 수정 실패")).build()
                            )
                    )
            );
            actions.andExpect(status().is(500));
        }

    }

}




