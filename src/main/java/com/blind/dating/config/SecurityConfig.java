package com.blind.dating.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        return http
                .csrf().disable()
                .cors().and()
                .authorizeRequests(auth->auth
                        .antMatchers("/h2-console/**", "/swagger-ui/**","/v3/api-docs/**").permitAll()
                        .antMatchers("/api/login","/api/signup").permitAll()
                        .anyRequest().authenticated())
                .headers().frameOptions().sameOrigin()
                .and().build();
    }
}
