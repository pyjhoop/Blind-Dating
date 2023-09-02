package com.blind.dating.security;

import com.blind.dating.repository.RefreshTokenRepository;
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

import javax.security.sasl.AuthenticationException;
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
    private final RefreshTokenRepository refreshTokenRepository;
    private final String signUpUrl = "/api/signup";
    private final String loginUrl = "/api/login";
    private final String checkIdUrl = "/api/check-userId";
    private final String checkNicknameUrl = "/api/check-nickname";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
           if(isSignUpRequest(request)){

           }else{
               try{
                   //엑세스 토큰
                   String token = parseBearerToken(request);

                   String refreshToken = tokenProvider.getRefreshToken(request);
                   String id = tokenProvider.validateAndGetUserId(refreshToken);

                   String storedToken = refreshTokenRepository.getRefreshToken(id);
                   if(!refreshToken.equals(storedToken)){
                       request.setAttribute("error","다른 기기에서 로그인하여 현재기기에서 로그아웃 합니다.");
                       throw new AuthenticationException("다른 기기에서 로그인하여 현재기기에서 로그아웃 합니다.");
                   }


                   // 토큰 검증하기 JWT이므로 인가 서버에 요청하지 않아도됨
                   if(token != null && tokenProvider.validateToken(token)){
                       String userId = tokenProvider.validateAndGetUserId(token);

                       // setContext 에 인증객체 저장하기.
                       Authentication authentication = new UsernamePasswordAuthenticationToken(userId, "",null);

                       SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
                       securityContext.setAuthentication(authentication);
                       SecurityContextHolder.setContext(securityContext);
                   }else{
                       throw new AuthenticationServiceException("Invalid or missing token");
                   }
               }catch (ExpiredJwtException e){
                   request.setAttribute("error","Jwt 토큰이 만료되었습니다.");

               }catch (Exception e){

               }

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
        return request.getRequestURI().equals(signUpUrl) && request.getMethod().equals("POST")
                || request.getRequestURI().equals(checkIdUrl) && request.getMethod().equals("POST")
                || request.getRequestURI().equals(loginUrl)&& request.getMethod().equals("POST")
                || request.getRequestURI().contains(checkNicknameUrl) && request.getMethod().equals("GET");

    }
}
