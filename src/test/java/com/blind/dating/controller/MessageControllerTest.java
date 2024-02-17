package com.blind.dating.controller;

import com.blind.dating.config.SecurityConfig;
import com.blind.dating.domain.interest.Interest;
import com.blind.dating.domain.message.MessageController;
import com.blind.dating.domain.message.MessageService;
import com.blind.dating.domain.user.Role;
import com.blind.dating.domain.user.UserAccount;
import com.blind.dating.dto.message.MessageRequestDto;
import com.blind.dating.dto.message.MessageResponseDto;
import com.blind.dating.dto.message.MessageStatus;
import com.blind.dating.security.TokenProvider;
import com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper;
import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.epages.restdocs.apispec.Schema;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

import static com.epages.restdocs.apispec.ResourceDocumentation.headerWithName;
import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("MessageController - 테스트")
@WebMvcTest(MessageController.class)
@Import(SecurityConfig.class)
@ActiveProfiles("test")
class MessageControllerTest extends ControllerTestConfig{

    @MockBean private MessageService messageService;
    @SpyBean private TokenProvider tokenProvider;
    @Autowired private MockMvc mvc;
    @Autowired private ObjectMapper objectMapper;

    private String accessToken;
    private MessageRequestDto messageRequestDto;
    private UserAccount user;
    private MessageResponseDto messageResponseDto;

    @BeforeEach
    void setting() {
        user = new UserAccount(1L,"userId","userPassword","nickname","서울","INTP","M",false, "안녕", LocalDateTime.now(), Role.USER,null,"origin","change", null);
        user.setInterests(List.of(new Interest()));
        accessToken = tokenProvider.create(user);
        messageRequestDto = new MessageRequestDto(1L, "안녕하세요");
        messageResponseDto = new MessageResponseDto(1L, 1L, "nickname","안녕하세요 반갑습니다.", MessageStatus.WAIT);
    }

    @Nested
    @DisplayName("메시지 전송")
    class PostMessage {
        @Test
        @DisplayName("전송 성공")
        void givenMessageRequest_whenSendMessage_thenSuccess() throws Exception {

            mvc.perform(
                RestDocumentationRequestBuilders.post("/api/messages")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(messageRequestDto))
                    .accept(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer "+accessToken)
            ).andExpect(status().isOk())
                    .andDo(
                MockMvcRestDocumentationWrapper.document("메시지 전송 - 성공",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    resource(
                        ResourceSnippetParameters.builder()
                        .description("메시지 전송 성공")
                        .tag("Message").summary("메시지 전송 API")
                        .requestFields()
                        .requestHeaders(
                                headerWithName("Authorization").description("Basic auth credentials")
                        )
                        .responseFields(
                                fieldWithPath("code").description("응답 코드"),
                                fieldWithPath("status").description("응답 상태"),
                                fieldWithPath("message").description("응답 메시지"),
                                fieldWithPath("data").description("응답 데이터")
                        ).responseSchema(Schema.schema("메시지 전송 응답")).build()
                    )
                )
            );
        }

        @Test
        @DisplayName("인증 실패")
        void givenMessageRequest_whenSendMessage_thenReturn401() throws Exception {

            mvc.perform(
                RestDocumentationRequestBuilders.post("/api/messages")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(messageRequestDto))
                    .accept(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer "+"accessToken")
            ).andExpect(status().is(401))

            .andDo(
                MockMvcRestDocumentationWrapper.document("메시지 전송 - 인증 실패",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    resource(
                        ResourceSnippetParameters.builder()
                            .description("메시지 전송 인증 실패")
                            .tag("Message").summary("메시지 전송 API")
                            .requestFields()
                            .requestHeaders(
                                    headerWithName("Authorization").description("Basic auth credentials")
                            )
                            .responseFields(
                                    fieldWithPath("code").description("응답 코드"),
                                    fieldWithPath("status").description("응답 상태"),
                                    fieldWithPath("message").description("응답 메시지"),
                                    fieldWithPath("data").description("응답 데이터")
                            ).responseSchema(Schema.schema("메시지 전송 응답")).build()
                    )
                )
            );
        }

        @Test
        @DisplayName("서버 오류")
        void givenMessageRequest_whenSendMessage_thenReturn500() throws Exception {

            doThrow(new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error"))
                    .when(messageService).postMessage(any(Authentication.class), any(MessageRequestDto.class));

            mvc.perform(
                RestDocumentationRequestBuilders.post("/api/messages")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(messageRequestDto))
                    .accept(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer "+accessToken))
                .andExpect(status().is(500))

            .andDo(
                MockMvcRestDocumentationWrapper.document("메시지 전송 - 서버 오류",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    resource(
                    ResourceSnippetParameters.builder()
                            .description("메시지 전송 서버 오류")
                            .tag("Message").summary("메시지 전송 API")
                            .requestFields()
                            .requestHeaders(
                                    headerWithName("Authorization").description("Basic auth credentials")
                            )
                            .responseFields(
                                    fieldWithPath("code").description("응답 코드"),
                                    fieldWithPath("status").description("응답 상태"),
                                    fieldWithPath("message").description("응답 메시지"),
                                    fieldWithPath("data").description("응답 데이터")
                            ).responseSchema(Schema.schema("메시지 전송 응답")).build()
                    )
                )
            );
        }
    }

    @Nested
    @DisplayName("메시지 수락")
    class AcceptMessage {
        @Test
        @DisplayName("수락 성공")
        void givenMessageId_whenAcceptMessage_thenSuccess() throws Exception {

            mvc.perform(
                RestDocumentationRequestBuilders.patch("/api/messages/{messageId}/accept", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer "+accessToken)
            ).andExpect(status().isOk())
            .andDo(
                MockMvcRestDocumentationWrapper.document("메시지 수락 - 성공",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    resource(
                        ResourceSnippetParameters.builder()
                            .description("메시지 수락")
                            .tag("Message").summary("메시지 수락 API")
                            .requestFields()
                            .requestHeaders(
                                    headerWithName("Authorization").description("Basic auth credentials")
                            )
                            .responseFields(
                                    fieldWithPath("code").description("응답 코드"),
                                    fieldWithPath("status").description("응답 상태"),
                                    fieldWithPath("message").description("응답 메시지"),
                                    fieldWithPath("data").description("응답 데이터")
                            ).responseSchema(Schema.schema("메시지 수락 응답")).build()
                    )
                )
            );
        }

        @Test
        @DisplayName("인증 실패")
        void givenMessageId_whenAcceptMessage_then401() throws Exception {

            mvc.perform(
            RestDocumentationRequestBuilders.patch("/api/messages/{messageId}/accept", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer "+"accessToken")
            ).andExpect(status().is(401))

            .andDo(
                MockMvcRestDocumentationWrapper.document("메시지 수락 - 인증 실패",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    resource(
                        ResourceSnippetParameters.builder()
                            .description("메시지 수락")
                            .tag("Message").summary("메시지 수락 API")
                            .requestFields()
                            .requestHeaders(
                                    headerWithName("Authorization").description("Basic auth credentials")
                            )
                            .responseFields(
                                    fieldWithPath("code").description("응답 코드"),
                                    fieldWithPath("status").description("응답 상태"),
                                    fieldWithPath("message").description("응답 메시지"),
                                    fieldWithPath("data").description("응답 데이터")
                            ).responseSchema(Schema.schema("메시지 수락 응답")).build()
                    )
                )
            );

        }
        @Test
        @DisplayName("서버 에러")
        void givenMessageId_whenAcceptMessage_then501() throws Exception {

            doThrow(new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error"))
                    .when(messageService).acceptMessage(any(Authentication.class), anyLong());

            mvc.perform(
                RestDocumentationRequestBuilders.patch("/api/messages/{messageId}/accept", 1)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer " + accessToken)
            ).andExpect(status().is(500))

            .andDo(
                MockMvcRestDocumentationWrapper.document("메시지 수락 - 서버 에러",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    resource(
                        ResourceSnippetParameters.builder()
                        .description("메시지 수락")
                        .tag("Message").summary("메시지 수락 API")
                        .requestFields()
                        .requestHeaders(
                                headerWithName("Authorization").description("Basic auth credentials")
                        )
                        .responseFields(
                                fieldWithPath("code").description("응답 코드"),
                                fieldWithPath("status").description("응답 상태"),
                                fieldWithPath("message").description("응답 메시지"),
                                fieldWithPath("data").description("응답 데이터")
                        ).responseSchema(Schema.schema("메시지 수락 응답")).build()
                    )
                )
            );
        }
    }
    
    @Nested
    @DisplayName("메시지 거절하기")
    class RejectMessage {
        @Test
        @DisplayName("거절 성공")
        void givenMessageId_whenRejectMessage_thenSuccess() throws Exception {

            mvc.perform(
                RestDocumentationRequestBuilders.patch("/api/messages/{messageId}/reject", 1)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer "+accessToken)
            ).andExpect(status().isOk())
                    
            .andDo(
                MockMvcRestDocumentationWrapper.document("메시지 거절 - 성공",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    resource(
                        ResourceSnippetParameters.builder()
                            .description("메시지 거절")
                            .tag("Message").summary("메시지 거절 API")
                            .requestFields()
                            .requestHeaders(
                                    headerWithName("Authorization").description("Basic auth credentials")
                            )
                            .responseFields(
                                    fieldWithPath("code").description("응답 코드"),
                                    fieldWithPath("status").description("응답 상태"),
                                    fieldWithPath("message").description("응답 메시지"),
                                    fieldWithPath("data").description("응답 데이터")
                            ).responseSchema(Schema.schema("메시지 거절 응답")).build()
                    )
                )
            );
        }

        @Test
        @DisplayName("인증 실패")
        void givenMessageId_whenRejectMessage_then401() throws Exception {

            mvc.perform(
                RestDocumentationRequestBuilders.patch("/api/messages/{messageId}/reject", 1)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer "+"accessToken")
            ).andExpect(status().is(401))

            .andDo(
                MockMvcRestDocumentationWrapper.document("메시지 거절 - 인증 실패",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    resource(
                    ResourceSnippetParameters.builder()
                        .description("메시지 거절")
                        .tag("Message").summary("메시지 거절 API")
                        .requestFields()
                        .requestHeaders(
                                headerWithName("Authorization").description("Basic auth credentials")
                        )
                        .responseFields(
                                fieldWithPath("code").description("응답 코드"),
                                fieldWithPath("status").description("응답 상태"),
                                fieldWithPath("message").description("응답 메시지"),
                                fieldWithPath("data").description("응답 데이터")
                        ).responseSchema(Schema.schema("메시지 거절 응답")).build()
                    )
                )
            );

        }
        @Test
        @DisplayName("서버 에러")
        void givenMessageId_whenAcceptMessage_then501() throws Exception {

            doThrow(new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error"))
                    .when(messageService).rejectMessage(any(Authentication.class), anyLong());

            mvc.perform(
                RestDocumentationRequestBuilders.patch("/api/messages/{messageId}/reject", 1)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer " + accessToken)
            ).andExpect(status().is(500))

            .andDo(
                MockMvcRestDocumentationWrapper.document("메시지 거절 - 서버 에러",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    resource(
                    ResourceSnippetParameters.builder()
                        .description("메시지 거절")
                        .tag("Message").summary("메시지 거절 API")
                        .requestFields()
                        .requestHeaders(
                                headerWithName("Authorization").description("Basic auth credentials")
                        )
                        .responseFields(
                                fieldWithPath("code").description("응답 코드"),
                                fieldWithPath("status").description("응답 상태"),
                                fieldWithPath("message").description("응답 메시지"),
                                fieldWithPath("data").description("응답 데이터")
                        ).responseSchema(Schema.schema("메시지 수락 응답")).build()
                    )
                )
            );
        }
    }

    @Nested
    @DisplayName("내게온 메시지 조회")
    class GetMessagesToMe {

        @Test
        @DisplayName("조회 성공")
        void givenNothing_whenGetMessagesToMe_thenReturnSuccess() throws Exception {
            given(messageService.getMessageToMe(any(Authentication.class)))
                    .willReturn(List.of(messageResponseDto));

            mvc.perform(
                RestDocumentationRequestBuilders.get("/api/messages/me")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer "+accessToken)
            ).andExpect(status().isOk())

            .andDo(
                MockMvcRestDocumentationWrapper.document("내게온 메시지 조회 - 성공",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    resource(
                    ResourceSnippetParameters.builder()
                        .description("내게온 메시지 조회")
                        .tag("Message").summary("내게 온 메시지 조회 API")
                        .requestFields()
                        .requestHeaders(
                                headerWithName("Authorization").description("Basic auth credentials")
                        )
                        .responseFields(
                                fieldWithPath("code").description("응답 코드"),
                                fieldWithPath("status").description("응답 상태"),
                                fieldWithPath("message").description("응답 메시지"),
                                fieldWithPath("data").description("응답 데이터"),
                                fieldWithPath("data[].id").description("메시지 번호"),
                                fieldWithPath("data[].userId").description("보낸 유저의 번호"),
                                fieldWithPath("data[].userNickname").description("보낸 유저 닉네임"),
                                fieldWithPath("data[].content").description("메시지 내용"),
                                fieldWithPath("data[].status").description("메시지 상태")
                        ).responseSchema(Schema.schema("메시지리스트 조회 응답")).build()
                    )
                )
            );

        }

        @Test
        @DisplayName("인증 실패")
        void givenNothing_whenGetMessagesToMe_thenReturn401() throws Exception {

            mvc.perform(
                RestDocumentationRequestBuilders.get("/api/messages/me")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer "+"accessToken")
            ).andExpect(status().is(401))

            .andDo(
                MockMvcRestDocumentationWrapper.document("내게온 메시지 조회 - 인증 실패",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    resource(
                    ResourceSnippetParameters.builder()
                        .description("내게온 메시지 조회")
                        .tag("Message").summary("내게 온 메시지 조회 API")
                        .requestFields()
                        .requestHeaders(
                            headerWithName("Authorization").description("Basic auth credentials")
                        )
                        .responseFields(
                            fieldWithPath("code").description("응답 코드"),
                            fieldWithPath("status").description("응답 상태"),
                            fieldWithPath("message").description("응답 메시지"),
                            fieldWithPath("data").description("응답 데이터")
                        ).responseSchema(Schema.schema("메시지리스트 조회 실패 응답")).build()
                    )
                )
            );

        }

        @Test
        @DisplayName("서버 오류")
        void givenNothing_whenGetMessagesToMe_thenReturn500() throws Exception {

            doThrow(new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error"))
                    .when(messageService).getMessageToMe(any(Authentication.class));

            mvc.perform(
                RestDocumentationRequestBuilders.get("/api/messages/me")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer "+accessToken)
            ).andExpect(status().is(500))

            .andDo(
                MockMvcRestDocumentationWrapper.document("내게온 메시지 조회 - 서버 오류",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    resource(
                        ResourceSnippetParameters.builder()
                            .description("내게온 메시지 조회")
                            .tag("Message").summary("내게 온 메시지 조회 API")
                            .requestFields()
                            .requestHeaders(
                                headerWithName("Authorization").description("Basic auth credentials")
                            )
                            .responseFields(
                                fieldWithPath("code").description("응답 코드"),
                                fieldWithPath("status").description("응답 상태"),
                                fieldWithPath("message").description("응답 메시지"),
                                fieldWithPath("data").description("응답 데이터")
                            ).responseSchema(Schema.schema("메시지리스트 조회 실패 응답")).build()
                    )
                )
            );

        }
    }

    @Nested
    @DisplayName("내가 보낸 메시지 조회")
    class GetMessagesFromMe {
        @Test
        @DisplayName("조회 성공")
        void givenNothing_whenGetMessagesFromMe_thenReturnSuccess() throws Exception {
            given(messageService.getMessageFromMe(any(Authentication.class)))
                    .willReturn(List.of(messageResponseDto));

            mvc.perform(
                RestDocumentationRequestBuilders.get("/api/messages")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer "+accessToken)
            ).andExpect(status().isOk())

            .andDo(
                MockMvcRestDocumentationWrapper.document("내가 보낸 메시지 조회 - 성공",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    resource(
                        ResourceSnippetParameters.builder()
                            .description("내가 보낸 메시지 조회")
                            .tag("Message").summary("내가 보낸 메시지 조회 API")
                            .requestFields()
                            .requestHeaders(
                                headerWithName("Authorization").description("Basic auth credentials")
                            )
                            .responseFields(
                                fieldWithPath("code").description("응답 코드"),
                                fieldWithPath("status").description("응답 상태"),
                                fieldWithPath("message").description("응답 메시지"),
                                fieldWithPath("data").description("응답 데이터"),
                                fieldWithPath("data[].id").description("메시지 번호"),
                                fieldWithPath("data[].userId").description("보낸 유저의 번호"),
                                fieldWithPath("data[].userNickname").description("보낸 유저 닉네임"),
                                fieldWithPath("data[].content").description("메시지 내용"),
                                fieldWithPath("data[].status").description("메시지 상태")
                            ).responseSchema(Schema.schema("메시지리스트 조회 응답")).build()
                    )
                )
            );

        }

        @Test
        @DisplayName("인증 실패")
        void givenNothing_whenGetMessagesFromMe_thenReturn401() throws Exception {

            mvc.perform(
                RestDocumentationRequestBuilders.get("/api/messages")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer "+"accessToken")
            ).andExpect(status().is(401))

            .andDo(
                MockMvcRestDocumentationWrapper.document("내가 보낸 메시지 조회 - 인증 실패",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    resource(
                        ResourceSnippetParameters.builder()
                            .description("내가 보낸 메시지 조회")
                            .tag("Message").summary("내가 보낸 메시지 조회 API")
                            .requestFields()
                            .requestHeaders(
                                headerWithName("Authorization").description("Basic auth credentials")
                            )
                            .responseFields(
                                fieldWithPath("code").description("응답 코드"),
                                fieldWithPath("status").description("응답 상태"),
                                fieldWithPath("message").description("응답 메시지"),
                                fieldWithPath("data").description("응답 데이터")
                            ).responseSchema(Schema.schema("메시지리스트 조회 실패 응답")).build()
                    )
                )
            );

        }

        @Test
        @DisplayName("서버 오류")
        void givenNothing_whenGetMessagesToMe_thenReturn500() throws Exception {

            doThrow(new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error"))
                    .when(messageService).getMessageFromMe(any(Authentication.class));

            mvc.perform(
                RestDocumentationRequestBuilders.get("/api/messages")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer "+accessToken)
            ).andExpect(status().is(500))

            .andDo(
                MockMvcRestDocumentationWrapper.document("내가 보낸 메시지 조회 - 서버 오류",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    resource(
                        ResourceSnippetParameters.builder()
                            .description("내가 보낸 메시지 조회")
                            .tag("Message").summary("내가 보낸 메시지 조회 API")
                            .requestFields()
                            .requestHeaders(
                                headerWithName("Authorization").description("Basic auth credentials")
                            )
                            .responseFields(
                                fieldWithPath("code").description("응답 코드"),
                                fieldWithPath("status").description("응답 상태"),
                                fieldWithPath("message").description("응답 메시지"),
                                fieldWithPath("data").description("응답 데이터")
                            ).responseSchema(Schema.schema("메시지리스트 조회 실패 응답")).build()
                    )
                )
            );

        }
    }
    


}



