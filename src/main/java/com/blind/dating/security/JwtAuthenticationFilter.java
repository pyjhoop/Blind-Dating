package com.blind.dating.security;

import com.blind.dating.service.CustomUserDetailsService;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final TokenProvider tokenProvider;

    String[] singUpRequestUrls = {"/api/signup","/api/login", "/api/check-userId","/api/check-nickname","/swagger-ui"};

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        log.info(request.getRequestURI());
        try{
            if(!isSignUpRequest(request))
            {
                //엑세스 토큰
                String token = parseBearerToken(request);

                // 토큰 검증하기 JWT이므로 인가 서버에 요청하지 않아도됨
                if(token != null && tokenProvider.validateToken(token, request)){

                    // setContext 에 인증객체 저장하기.
                    Authentication authentication = tokenProvider.getAuthentication(token);
                    SecurityContextHolder.getContext().setAuthentication(authentication);

                }
            }

        }catch (Exception e){

        }

        filterChain.doFilter(request,response);

    }

    private String parseBearerToken(HttpServletRequest request){
        String bearerToken = request.getHeader("Authorization");

        if(bearerToken.startsWith("Bearer")){
            return bearerToken.substring(7);
        }
        return null;
    }

    private boolean isSignUpRequest(HttpServletRequest request) {

        String requestUri = request.getRequestURI();

        for(String uri: singUpRequestUrls){
            if(uri.contains(requestUri))
                return true;
        }

        return false;
    }
}
