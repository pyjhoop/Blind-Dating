package com.blind.dating.controller;

import com.blind.dating.config.SecurityConfig;
import com.blind.dating.domain.token.TokenController;
import com.blind.dating.dto.interest.InterestDto;
import com.blind.dating.domain.user.dto.LogInResponse;
import com.blind.dating.security.TokenProvider;
import com.blind.dating.domain.token.TokenService;
import com.blind.dating.util.CookieUtil;
import com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper;
import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.epages.restdocs.apispec.Schema;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.web.server.ResponseStatusException;


import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static com.epages.restdocs.apispec.ResourceDocumentation.resource;

@DisplayName("TokenController - 테스트")
@WebMvcTest(TokenController.class)
@Import(SecurityConfig.class)
class TokenControllerTest extends ControllerTestConfig{

    @Autowired MockMvc mvc;
    @MockBean private TokenService tokenService;
    @MockBean private TokenProvider tokenProvider;
    @MockBean private CookieUtil cookieUtil;

    private LogInResponse logInResponse;
    private Cookie cookie;

    @BeforeEach
    void setting() {
        List<InterestDto> interests = List.of(InterestDto.of(1L,"자전거 타기"),
                InterestDto.of(2L, "놀기"), InterestDto.of(3L,"게임하기"));

        logInResponse = new LogInResponse(1L, "userId","nickname","서울","INTP","M", interests,"하이요","accessToken","refreshToken","/profile");
        cookie = new Cookie("refreshToken", "refreshToken");
    }

    @Nested
    @DisplayName("토큰 재발급")
    class ReissueTokens {
        @DisplayName("성공")
        @Test
        void testRegenerateRefreshTokenThen200() throws Exception {
            //Given
            String userId = "1";
            given(tokenService.validRefreshToken(any(Cookie.class))).willReturn(userId);
            given(tokenService.updateRefreshToken(userId)).willReturn(logInResponse);

            ResultActions actions = mvc.perform(
                    RestDocumentationRequestBuilders.get("/api/refresh")
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON)
                            .cookie(cookie)
            ).andDo(
                    MockMvcRestDocumentationWrapper.document("토큰 재발급 - 성공",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            resource(
                                    ResourceSnippetParameters.builder()
                                            .description("토큰 재발급 API")
                                            .tag("Tokens").description("토큰 재발급")
                                            .requestFields()
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
                                                    fieldWithPath("data.accessToken").description("AccessToken"),
                                                    fieldWithPath("data.profile").description("프로필 이미지")
                                            ).responseSchema(Schema.schema("토큰 재발급 성공")).build()
                            )
                    )
            );
            actions.andExpect(status().isOk());
        }

        @DisplayName("리프레시 토큰 없어서 실패")
        @Test
        void testRegenerateRefreshTokenThen400() throws Exception {
            //Given
            String userId = "1";
            given(tokenService.validRefreshToken(any(Cookie.class))).willReturn(userId);
            given(tokenService.updateRefreshToken(userId)).willThrow(new RuntimeException(userId+"에 해당하는 유저는 존재하지 않습니다."));

            ResultActions actions = mvc.perform(
                    RestDocumentationRequestBuilders.get("/api/refresh")
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON)
                            .cookie(cookie)
                            .header("Authorization", "Bearer "+"refreshToken")
            ).andDo(
                    MockMvcRestDocumentationWrapper.document("토큰 재발급 - 실패",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            resource(
                                    ResourceSnippetParameters.builder()
                                            .description("토큰 재발급 API")
                                            .tag("Tokens").description("토큰 재발급")
                                            .requestFields()
                                            .responseFields(
                                                    fieldWithPath("code").description("응답 코드"),
                                                    fieldWithPath("status").description("응답 상태"),
                                                    fieldWithPath("message").description("응답 메시지"),
                                                    fieldWithPath("data").description("응답 데이터")
                                            ).responseSchema(Schema.schema("토큰 재발급 실패")).build()
                            )
                    )
            );
            actions.andExpect(status().is(400));
        }

        @DisplayName("리프레시 서버 에러")
        @Test
        void testRegenerateRefreshTokenThen500() throws Exception {
            //Given
            String userId = "1";
            given(tokenService.validRefreshToken(any(Cookie.class)))
                    .willThrow(new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error"));

            ResultActions actions = mvc.perform(
                    RestDocumentationRequestBuilders.get("/api/refresh")
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON)
                            .cookie(cookie)
                            .header("Authorization", "Bearer "+"refreshToken")
            ).andDo(
                    MockMvcRestDocumentationWrapper.document("토큰 재발급 - 서버 에러",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            resource(
                                    ResourceSnippetParameters.builder()
                                            .description("토큰 재발급 API")
                                            .tag("Tokens").description("토큰 재발급")
                                            .requestFields()
                                            .responseFields(
                                                    fieldWithPath("code").description("응답 코드"),
                                                    fieldWithPath("status").description("응답 상태"),
                                                    fieldWithPath("message").description("응답 메시지"),
                                                    fieldWithPath("data").description("응답 데이터")
                                            ).responseSchema(Schema.schema("토큰 재발급 실패")).build()
                            )
                    )
            );
            actions.andExpect(status().is(500));
        }
    }


}