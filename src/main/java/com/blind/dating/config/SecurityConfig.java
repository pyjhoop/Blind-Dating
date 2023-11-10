package com.blind.dating.config;

import com.blind.dating.security.CustomAccessDeniedHandler;
import com.blind.dating.security.CustomAuthenticationEntryPoint;
import com.blind.dating.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.filter.CorsFilter;

@RequiredArgsConstructor
@Configuration
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        return http.addFilterAfter(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
        .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .csrf().disable()
                .cors().and()
                .authorizeRequests(auth->auth
                        .antMatchers("/h2-console/**", "/swagger-ui/**","/v3/api-docs/**").permitAll()
                        .antMatchers("/api/login","/api/signup","/api/profile","/api/refresh", "/api/logout").permitAll()
                        .antMatchers("/chat/rooms","/chat/room","/stomp/chat/**","/sub/**","/pub/**","/stomp/chatroom/**").permitAll()
                        .antMatchers("/api/check-nickname/**","/api/check-userId").permitAll()
                        .anyRequest().authenticated())
                .headers().frameOptions().sameOrigin()
                .and().exceptionHandling()
                .accessDeniedHandler(new CustomAccessDeniedHandler())
                .authenticationEntryPoint(new CustomAuthenticationEntryPoint()).and()
                .build();
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder(){
        return new BCryptPasswordEncoder();
    }
}
