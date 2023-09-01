package com.blind.dating.security;

import com.blind.dating.repository.RefreshTokenRepository;
import com.blind.dating.service.CustomUserDetailsService;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {


    private final TokenProvider tokenProvider;
    private final CustomUserDetailsService customUserDetailsService;
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
                   //accessToken
                   String token = parseBearerToken(request);
                   //refreshToken
                   String refreshToken = getRefreshToken(request);
                   //refreshToken 있는지 비교하고 있으면 예외 발생시키기
                   String id = tokenProvider.validateAndGetUserId(token);
                   String oldRefreshToken = refreshTokenRepository.getRefreshToken(id);

                   if(!oldRefreshToken.equals(refreshToken)){
                       request.setAttribute("error","다른 기기에서 로그인해서 해당 기기에서 로그아웃 합니다.");
                       throw new RuntimeException("다른 기기에서 로그인해서 해당 기기에서 로그아웃 합니다.");
                   }
                   // 토큰 검증하기 JWT이므로 인가 서버에 요청하지 않아도됨
                   if(token != null && tokenProvider.validateToken(token)){
                       String userId = tokenProvider.validateAndGetUserId(token);
                       // SecurityContextHolder에 등록해야 인증된 사용자라고 생각한다.
                       Authentication authentication = tokenProvider.getAuthentication(token);
//                       AbstractAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
//                               userDetails, // 인증된 사용자의 정보. 문자열이 아니어도 아무거나 넣을 수 있다. 보통 UserDetails라는 오브젝트 넣는다.
//                               null,
//                               userDetails.getAuthorities()
//                       );
                       //authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                       SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
                       securityContext.setAuthentication(authentication);
                       SecurityContextHolder.setContext(securityContext);
                   }else{
                       throw new AuthenticationServiceException("Invalid or missing token");
                   }
               }catch (ExpiredJwtException e){

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

    private String getRefreshToken(HttpServletRequest request){
        Cookie[] cookies = request.getCookies();

        String refreshToken = "";

        for(Cookie c: cookies){
            if(c.getName().equals("refreshToken")){
                refreshToken = c.getValue();
                System.out.println(refreshToken);

            }
        }

        return refreshToken;

    }

    private boolean isSignUpRequest(HttpServletRequest request) {
        return request.getRequestURI().equals(signUpUrl) && request.getMethod().equals("POST")
                || request.getRequestURI().equals(checkIdUrl) && request.getMethod().equals("POST")
                || request.getRequestURI().equals(loginUrl)&& request.getMethod().equals("POST")
                || request.getRequestURI().contains(checkNicknameUrl) && request.getMethod().equals("GET");

    }
}
