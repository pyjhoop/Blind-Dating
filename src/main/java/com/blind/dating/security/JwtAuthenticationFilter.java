package com.blind.dating.security;

import com.blind.dating.service.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
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
    private final CustomUserDetailsService customUserDetailsService;
    private final String signUpUrl = "/api/signup";
    private final String loginUrl = "/api/login";
    private final String checkIdUrl = "/api/check-userId";
    private final String checkNicknameUrl = "/api/check-nickname";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
           if(isSignUpRequest(request)){

           }else{
               try{
                   String token = parseBearerToken(request);
                   log.warn(token);

                   // 토큰 검증하기 JWT이므로 인가 서버에 요청하지 않아도됨
                   if(token != null || !token.equalsIgnoreCase("null")){
                       String userId = tokenProvider.validateAndGetUserId(token);
                       // SecurityContextHolder에 등록해야 인증된 사용자라고 생각한다.
                       UserDetails userDetails = customUserDetailsService.loadUserByUsername(userId);
                       AbstractAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                               userDetails, // 인증된 사용자의 정보. 문자열이 아니어도 아무거나 넣을 수 있다. 보통 UserDetails라는 오브젝트 넣는다.
                               null,
                               userDetails.getAuthorities()
                       );
                       authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                       SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
                       securityContext.setAuthentication(authentication);
                       SecurityContextHolder.setContext(securityContext);
                   }else{
                       throw new AuthenticationServiceException("Invalid or missing token");
                   }
               }catch (Exception e){
                   logger.error("Could not set user authentication in security context", e);

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
