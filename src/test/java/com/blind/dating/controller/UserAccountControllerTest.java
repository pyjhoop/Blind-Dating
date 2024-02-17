package com.blind.dating.controller;

import com.blind.dating.common.code.UserResponseCode;
import com.blind.dating.config.SecurityConfig;
import com.blind.dating.domain.interest.Interest;
import com.blind.dating.domain.user.*;
import com.blind.dating.domain.user.dto.*;
import com.blind.dating.dto.interest.InterestDto;
import com.blind.dating.security.JwtAuthenticationFilter;
import com.blind.dating.security.TokenProvider;
import com.blind.dating.util.CookieUtil;
import com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper;
import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.epages.restdocs.apispec.Schema;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.Test;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static com.epages.restdocs.apispec.ResourceDocumentation.resource;

@DisplayName("UserAccountController - 테스트")
@Import(SecurityConfig.class)
@WebMvcTest(UserAccountController.class)
class UserAccountControllerTest extends ControllerTestConfig{

    @Autowired
    private MockMvc mvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Spy
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @MockBean private UserAccountService userAccountService;
    @MockBean private TokenProvider tokenProvider;
    @MockBean private JwtAuthenticationFilter jwtAuthenticationFilter;
    @MockBean private CustomUserDetailsService customUserDetailsService;
    @MockBean private CookieUtil cookieUtil;



    private UserRequestDto dto;
    private UserAccount user;
    @BeforeEach
    void setting(){
        dto = UserRequestDto.of("user01","userPass01","userNickname","서울","INFP","M","안녕하세요");
        dto.setInterests(List.of("코딩하기","게임하기"));
        user = new UserAccount(1L, "userId","password","nickname","서울","INTP","M",false,"하이요",null, Role.USER,"K","origin", "change", null);
        user.setRecentLogin(LocalDateTime.now());
        user.setUserPassword(bCryptPasswordEncoder.encode(dto.getUserPassword()));
        user.setInterests(List.of(new Interest()));
    }

    @Nested
    @DisplayName("회원가입")
    class UserRegister {
        @DisplayName("회원가입 성공")
        @Test
        void givenUserRequest_whenRegister_thenSuccess() throws Exception {

            given(userAccountService.register(any(UserRequestDto.class))).willReturn(user);

            ResultActions actions = mockMvc.perform(
                    RestDocumentationRequestBuilders.post("/api/signup")
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto))
            ).andDo(
                    MockMvcRestDocumentationWrapper.document("회원가입-성공",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            resource(
                                    ResourceSnippetParameters.builder()
                                            .description("회원가입 API")
                                            .tag("UserAccount").description("회원가입 API")
                                            .requestFields(
                                                    fieldWithPath("userId").description("유저 아이디"),
                                                    fieldWithPath("userPassword").description("비밀번호"),
                                                    fieldWithPath("nickname").description("닉네임"),
                                                    fieldWithPath("region").description("사는 지역"),
                                                    fieldWithPath("mbti").description("MBTI"),
                                                    fieldWithPath("gender").description("성별"),
                                                    fieldWithPath("interests").description("관심사"),
                                                    fieldWithPath("selfIntroduction").description("자기소개")
                                            ).requestSchema(Schema.schema("회원가입 요청"))
                                            .responseFields(
                                                    fieldWithPath("code").description("응답 코드"),
                                                    fieldWithPath("status").description("응답 상태"),
                                                    fieldWithPath("message").description("응답 메시지"),
                                                    fieldWithPath("data").description("응답 데이터")
                                            ).responseSchema(Schema.schema("회원가입 성공 응답"))
                                            .build()
                            )
                    )
            );

            actions.andExpect(status().isOk());
        }
        @DisplayName("유저아이디로 인한 실패")
        @Test
        void givenUserRequest_whenRegister_thenErrors() throws Exception {
            // Given
            dto.setUserId("use");

            ResultActions actions = mockMvc.perform(
                    RestDocumentationRequestBuilders.post("/api/signup")
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto))
            ).andDo(
                    MockMvcRestDocumentationWrapper.document("userId로인한 예외발생",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            resource(
                                    ResourceSnippetParameters.builder()
                                            .description("회원가입 API")
                                            .tag("UserAccount").description("회원가입 API")
                                            .requestFields(
                                                    fieldWithPath("userId").description("유저 아이디"),
                                                    fieldWithPath("userPassword").description("비밀번호"),
                                                    fieldWithPath("nickname").description("닉네임"),
                                                    fieldWithPath("region").description("사는 지역"),
                                                    fieldWithPath("mbti").description("MBTI"),
                                                    fieldWithPath("gender").description("성별"),
                                                    fieldWithPath("interests").description("관심사"),
                                                    fieldWithPath("selfIntroduction").description("자기소개")
                                            ).requestSchema(Schema.schema("회원가입 요청"))
                                            .responseFields(
                                                    fieldWithPath("code").description("응답 코드"),
                                                    fieldWithPath("status").description("응답 상태"),
                                                    fieldWithPath("message").description("응답 메시지"),
                                                    fieldWithPath("data").description("응답 데이터"),
                                                    fieldWithPath("data.userId").description("아이디 관련 에러")
                                            ).responseSchema(Schema.schema("회원가입 실패 응답"))
                                            .build()
                            )
                    )

            );

            actions.andExpect(status().isBadRequest());

        }

        @DisplayName("서버 오류")
        @Test
        void givenUserRequest_whenRegister_thenServerError() throws Exception {
            // Given
            dto.setUserId("use");
            given(userAccountService.register(any(UserRequestDto.class)))
                    .willThrow(new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error"));


            ResultActions actions = mockMvc.perform(
                    RestDocumentationRequestBuilders.post("/api/signup")
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto))
            ).andDo(
                    MockMvcRestDocumentationWrapper.document("회원가입 - 서버 오류",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            resource(
                                    ResourceSnippetParameters.builder()
                                            .description("회원가입 API")
                                            .tag("UserAccount").description("회원가입 API")
                                            .requestFields(
                                                    fieldWithPath("userId").description("유저 아이디"),
                                                    fieldWithPath("userPassword").description("비밀번호"),
                                                    fieldWithPath("nickname").description("닉네임"),
                                                    fieldWithPath("region").description("사는 지역"),
                                                    fieldWithPath("mbti").description("MBTI"),
                                                    fieldWithPath("gender").description("성별"),
                                                    fieldWithPath("interests").description("관심사"),
                                                    fieldWithPath("selfIntroduction").description("자기소개")
                                            ).requestSchema(Schema.schema("회원가입 요청"))
                                            .responseFields(
                                                    fieldWithPath("code").description("응답 코드"),
                                                    fieldWithPath("status").description("응답 상태"),
                                                    fieldWithPath("message").description("응답 메시지"),
                                                    fieldWithPath("data").description("응답 데이터"),
                                                    fieldWithPath("data.userId").description("아이디 관련 에러")
                                            ).responseSchema(Schema.schema("회원가입 실패 응답"))
                                            .build()
                            )
                    )

            );

            actions.andExpect(status().isBadRequest());

        }
    }

    @Nested
    @DisplayName("유저 로그인")
    class UserLogin {
        @DisplayName("로그인 성공")
        @Test
        void givenLoginInfo_whenLogin_thenSuccess() throws Exception {
            String userId = "userId";
            String userPassword = "userPwd";
            LoginInputDto loginDto = new LoginInputDto(userId, userPassword);
            List<InterestDto> interests = List.of(InterestDto.of(1L,"자전거 타기"),
                    InterestDto.of(2L, "놀기"), InterestDto.of(3L,"게임하기"));

            LogInResponse response = LogInResponse.from(user, "accessToken", "refreshToken");
            response.setInterests(interests);

            given(userAccountService.getLoginInfo(userId, userPassword)).willReturn(response);

            ResultActions actions = mockMvc.perform(
                    RestDocumentationRequestBuilders.post("/api/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(loginDto))
            ).andDo(
                    MockMvcRestDocumentationWrapper.document("로그인 성공",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            resource(
                                    ResourceSnippetParameters.builder()
                                            .description("로그인 API")
                                            .tag("UserAccount").description("로그인 API")
                                            .requestFields(
                                                    fieldWithPath("userId").description("유저 아이디"),
                                                    fieldWithPath("userPassword").description("비밀번호")
                                            ).requestSchema(Schema.schema("로그인 요청"))
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
                                                    fieldWithPath("data.selfIntroduction").description("자기소개"),
                                                    fieldWithPath("data.accessToken").description("AccessToken")
                                            ).responseSchema(Schema.schema("로그인 성공 응답")).build()
                            )
                    )
            );

            actions.andExpect(status().isOk());
        }
        @DisplayName("로그인 실패")
        @Test
        void givenLoginInfo_whenLogin_thenThrowException() throws Exception {
            String userId = "userId";
            String userPassword = "userPwd";
            LoginInputDto loginDto = new LoginInputDto(userId, userPassword);

            given(userAccountService.getLoginInfo(userId, userPassword)).willThrow(new RuntimeException("Not Found UserInfo"));
            ResultActions actions = mockMvc.perform(
                    RestDocumentationRequestBuilders.post("/api/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(loginDto))
            ).andDo(
                    MockMvcRestDocumentationWrapper.document("로그인 실패",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            resource(
                                    ResourceSnippetParameters.builder()
                                            .description("로그인 API")
                                            .tag("UserAccount").description("로그인 API")
                                            .requestFields(
                                                    fieldWithPath("userId").description("유저 아이디"),
                                                    fieldWithPath("userPassword").description("비밀번호")
                                            ).requestSchema(Schema.schema("로그인 요청"))
                                            .responseFields(
                                                    fieldWithPath("code").description("응답 코드"),
                                                    fieldWithPath("status").description("응답 상태"),
                                                    fieldWithPath("message").description("응답 메시지"),
                                                    fieldWithPath("data").description("응답 데이터")
                                            ).responseSchema(Schema.schema("로그인 실패 응답")).build()
                            )
                    )
            );
            actions.andExpect(status().isBadRequest());
        }

        @DisplayName("서버 오류")
        @Test
        void givenLoginInfo_whenLogin_thenThrowServerException() throws Exception {
            String userId = "userId";
            String userPassword = "userPwd";
            LoginInputDto loginDto = new LoginInputDto(userId, userPassword);

            given(userAccountService.getLoginInfo(userId, userPassword))
                    .willThrow(new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error"));

            ResultActions actions = mockMvc.perform(
                    RestDocumentationRequestBuilders.post("/api/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(loginDto))
            ).andDo(
                    MockMvcRestDocumentationWrapper.document("로그인 실패 - 서버 오류",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            resource(
                                    ResourceSnippetParameters.builder()
                                            .description("로그인 API")
                                            .tag("UserAccount").description("로그인 API")
                                            .requestFields(
                                                    fieldWithPath("userId").description("유저 아이디"),
                                                    fieldWithPath("userPassword").description("비밀번호")
                                            ).requestSchema(Schema.schema("로그인 요청"))
                                            .responseFields(
                                                    fieldWithPath("code").description("응답 코드"),
                                                    fieldWithPath("status").description("응답 상태"),
                                                    fieldWithPath("message").description("응답 메시지"),
                                                    fieldWithPath("data").description("응답 데이터")
                                            ).responseSchema(Schema.schema("로그인 실패 응답")).build()
                            )
                    )
            );
            actions.andExpect(status().is(500));
        }
    }

    @Nested
    @DisplayName("유저아이디 중복 체크")
    class CheckUserId {
        @DisplayName("성공")
        @Test
        void givenUserId_whenCheckUserId_thenReturnNotUsed() throws Exception {
            // Given
            UserIdRequestDto dto = new UserIdRequestDto("userId");
            given(userAccountService.checkUserId(dto.getUserId())).willReturn(UserResponseCode.NOT_EXIST_USER_ID);

            ResultActions actions = mockMvc.perform(
                    RestDocumentationRequestBuilders.post("/api/check-userId")
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto))
            ).andDo(
                    MockMvcRestDocumentationWrapper.document("아이디 중복체크 - 없는 아이디",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            resource(
                                    ResourceSnippetParameters.builder()
                                            .description("아이디 중복 체크")
                                            .tag("UserAccount").description("아이디 중복 체크 API")
                                            .requestFields(
                                                    fieldWithPath("userId").description("유저 아이디")
                                            ).requestSchema(Schema.schema("아이디 중복체크 요청"))
                                            .responseFields(
                                                    fieldWithPath("code").description("응답 코드"),
                                                    fieldWithPath("status").description("응답 상태"),
                                                    fieldWithPath("message").description("응답 메시지"),
                                                    fieldWithPath("data").description("응답 데이터")
                                            ).responseSchema(Schema.schema("중복아이디 체크 응답")).build()
                            )
                    )
            );

            actions.andExpect(status().isOk());

        }

        @DisplayName("실패")
        @Test
        void givenUserId_whenCheckUserId_thenReturnUsed() throws Exception {
            // Given
            UserIdRequestDto dto = new UserIdRequestDto("userId");
            given(userAccountService.checkUserId(dto.getUserId())).willReturn(UserResponseCode.EXIST_USER_ID);

            ResultActions actions = mockMvc.perform(
                    RestDocumentationRequestBuilders.post("/api/check-userId")
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto))
            ).andDo(
                    MockMvcRestDocumentationWrapper.document("아이디 중복체크 - 있는 아이디",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            resource(
                                    ResourceSnippetParameters.builder()
                                            .description("아이디 중복 체크")
                                            .tag("UserAccount").description("아이디 중복 체크 API")
                                            .requestFields(
                                                    fieldWithPath("userId").description("유저 아이디")
                                            ).requestSchema(Schema.schema("아이디 중복체크 요청"))
                                            .responseFields(
                                                    fieldWithPath("code").description("응답 코드"),
                                                    fieldWithPath("status").description("응답 상태"),
                                                    fieldWithPath("message").description("응답 메시지"),
                                                    fieldWithPath("data").description("응답 데이터")
                                            ).responseSchema(Schema.schema("중복아이디 체크 응답")).build()
                            )
                    )
            );
            actions.andExpect(status().isOk());
        }

        @DisplayName("서버 오류")
        @Test
        void givenUserId_whenCheckUserId_thenReturn500() throws Exception {
            // Given
            UserIdRequestDto dto = new UserIdRequestDto("userId");
            given(userAccountService.checkUserId(dto.getUserId()))
                    .willThrow(new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error"));

            ResultActions actions = mockMvc.perform(
                    RestDocumentationRequestBuilders.post("/api/check-userId")
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto))
            ).andDo(
                    MockMvcRestDocumentationWrapper.document("아이디 중복체크 - 서버 오류",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            resource(
                                    ResourceSnippetParameters.builder()
                                            .description("중복아이디 체크 API")
                                            .tag("UserAccount").description("인증 관련 API")
                                            .requestFields(
                                                    fieldWithPath("userId").description("유저 아이디")
                                            ).requestSchema(Schema.schema("아이디 중복체크 요청"))
                                            .responseFields(
                                                    fieldWithPath("code").description("응답 코드"),
                                                    fieldWithPath("status").description("응답 상태"),
                                                    fieldWithPath("message").description("응답 메시지"),
                                                    fieldWithPath("data").description("응답 데이터")
                                            ).responseSchema(Schema.schema("중복아이디 체크 응답")).build()
                            )
                    )
            );
            actions.andExpect(status().is(500));
        }
    }

    @Nested
    @DisplayName("닉네임 중복 체크")
    class checkNickname {
        @DisplayName("없는 닉네임")
        @Test
        void givenUserId_whenCheckUserId_thenReturnNotUsed() throws Exception {
            // Given
            UserNicknameRequestDto dto = new UserNicknameRequestDto("nickname");
            given(userAccountService.checkNickname(dto.getNickname())).willReturn(UserResponseCode.NOT_EXIST_NICKNAME);

            ResultActions actions = mockMvc.perform(
                    RestDocumentationRequestBuilders.post("/api/check-nickname")
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto))
            ).andDo(
                    MockMvcRestDocumentationWrapper.document("닉네임 중복체크 - 없는 닉네임",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            resource(
                                    ResourceSnippetParameters.builder()
                                            .description("닉네임 중복체크 API")
                                            .tag("UserAccount").description("닉네임 중복체크 API")
                                            .requestFields(
                                                    fieldWithPath("nickname").description("닉네임")
                                            ).requestSchema(Schema.schema("닉네임 중복체크 요청"))
                                            .responseFields(
                                                    fieldWithPath("code").description("응답 코드"),
                                                    fieldWithPath("status").description("응답 상태"),
                                                    fieldWithPath("message").description("응답 메시지"),
                                                    fieldWithPath("data").description("응답 데이터")
                                            ).responseSchema(Schema.schema("닉네임 중복체크 응답")).build()
                            )
                    )
            );

            actions.andExpect(status().isOk());

        }

        @DisplayName("있는 닉네임")
        @Test
        void givenUserId_whenCheckUserId_thenReturnUsed() throws Exception {
            // Given
            UserNicknameRequestDto dto = new UserNicknameRequestDto("nickname");
            given(userAccountService.checkNickname(dto.getNickname())).willReturn(UserResponseCode.EXIST_NICKNAME);

            ResultActions actions = mockMvc.perform(
                    RestDocumentationRequestBuilders.post("/api/check-nickname")
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto))
            ).andDo(
                    MockMvcRestDocumentationWrapper.document("닉네임 중복체크 - 있는 닉네임",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            resource(
                                    ResourceSnippetParameters.builder()
                                            .description("닉네임 중복체크 API")
                                            .tag("UserAccount").description("닉네임 중복체크 API")
                                            .requestFields(
                                                    fieldWithPath("nickname").description("닉네임")
                                            ).requestSchema(Schema.schema("닉네임 중복체크 요청"))
                                            .responseFields(
                                                    fieldWithPath("code").description("응답 코드"),
                                                    fieldWithPath("status").description("응답 상태"),
                                                    fieldWithPath("message").description("응답 메시지"),
                                                    fieldWithPath("data").description("응답 데이터")
                                            ).responseSchema(Schema.schema("닉네임 중복체크 응답")).build()
                            )
                    )
            );

            actions.andExpect(status().isOk());
        }

        @DisplayName("서버 오류")
        @Test
        void givenUserId_whenCheckUserId_thenReturn500() throws Exception {
            // Given
            UserNicknameRequestDto dto = new UserNicknameRequestDto("nickname");
            given(userAccountService.checkNickname(dto.getNickname()))
                    .willThrow(new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error"));

            ResultActions actions = mockMvc.perform(
                    RestDocumentationRequestBuilders.post("/api/check-nickname")
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto))
            ).andDo(
                    MockMvcRestDocumentationWrapper.document("닉네임 중복체크 - 서버 오류",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            resource(
                                    ResourceSnippetParameters.builder()
                                            .description("닉네임 중복체크 API")
                                            .tag("UserAccount").description("닉네임 중복체크 API")
                                            .requestFields(
                                                    fieldWithPath("nickname").description("닉네임")
                                            ).requestSchema(Schema.schema("닉네임 중복체크 요청"))
                                            .responseFields(
                                                    fieldWithPath("code").description("응답 코드"),
                                                    fieldWithPath("status").description("응답 상태"),
                                                    fieldWithPath("message").description("응답 메시지"),
                                                    fieldWithPath("data").description("응답 데이터")
                                            ).responseSchema(Schema.schema("닉네임 중복체크 응답")).build()
                            )
                    )
            );

            actions.andExpect(status().is(500));
        }
    }




    @DisplayName("로그아웃 - 테스트")
    @Test
    void givenCookie_whenLogout_thenSuccess() throws Exception {
        Cookie cookie = new Cookie("refreshToken", "refreshToken");

        ResultActions actions = mockMvc.perform(
                RestDocumentationRequestBuilders.post("/api/logout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .cookie(cookie)
        ).andDo(
                MockMvcRestDocumentationWrapper.document("로그아웃",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        resource(
                                ResourceSnippetParameters.builder()
                                        .description("로그아웃 API")
                                        .tag("UserAccount").description("로그아웃 API")
                                        .requestFields()
                                        .responseFields(
                                                fieldWithPath("code").description("응답 코드"),
                                                fieldWithPath("status").description("응답 상태"),
                                                fieldWithPath("message").description("응답 메시지"),
                                                fieldWithPath("data").description("응답 데이터")
                                        ).responseSchema(Schema.schema("로그아웃 응답")).build()
                        )
                )
        );
        actions.andExpect(status().isOk());
    }

}