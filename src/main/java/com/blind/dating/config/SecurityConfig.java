package com.blind.dating.config;

import com.blind.dating.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.filter.CorsFilter;

@RequiredArgsConstructor
@Configuration
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        return http.addFilterAfter(
                        jwtAuthenticationFilter,
                        CorsFilter.class
                )
                .csrf().disable()
                .cors().and()
                .authorizeRequests(auth->auth
                        .antMatchers("/h2-console/**", "/swagger-ui/**","/v3/api-docs/**").permitAll()
                        .antMatchers("/api/login","/api/signup","/profile").permitAll()
                        .antMatchers("/api/check-nickname/**","/api/check-userId/**").permitAll()
                        .anyRequest().authenticated())
                .headers().frameOptions().sameOrigin()
                .and().build();
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder(){
        return new BCryptPasswordEncoder();
    }
}
