package com.blind.dating.controller;

import com.blind.dating.config.SecurityConfig;
import com.blind.dating.security.TokenProvider;
import com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper;
import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.epages.restdocs.apispec.Schema;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.web.servlet.ResultActions;

import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TestController.class)
@Import(SecurityConfig.class)
@AutoConfigureMockMvc(addFilters = false)
class TestControllerTest extends ControllerTestConfig{

    @MockBean private TokenProvider tokenProvider;

    @Test
    @DisplayName("테스트")
    void test() throws Exception{
        ResultActions resultActions = mockMvc.perform(
                RestDocumentationRequestBuilders.get("/api/test")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        ).andDo(
                MockMvcRestDocumentationWrapper.document("test-docs",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        resource(
                                ResourceSnippetParameters.builder()
                                        .description("테스트")
                                        .requestFields()
                                        .responseFields(
                                                fieldWithPath("status").description("ok")
                                        ).responseSchema(Schema.schema("Test")).build()
                        )
                        )
        );

        resultActions.andExpect(status().isOk());
    }
}