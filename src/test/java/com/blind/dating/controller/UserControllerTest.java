package com.blind.dating.controller;

import com.blind.dating.config.SecurityConfig;
import com.blind.dating.security.JwtAuthenticationFilter;
import com.blind.dating.security.TokenProvider;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithSecurityContext;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@Import(SecurityConfig.class)
@WebMvcTest(UserController.class)
public class UserControllerTest {

    @MockBean private UserController userController;
    @MockBean private TokenProvider tokenProvider;
    @MockBean private JwtAuthenticationFilter jwtAuthenticationFilter;
    private MockMvc mvc;

    @Test
    @WithUserDetails
    void test() throws Exception {

        mvc.perform(get("/api/hello"))
                .andExpect(status().isOk());

    }

}
