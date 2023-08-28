package com.blind.dating.security;

import com.blind.dating.domain.UserAccount;
import com.blind.dating.dto.user.UserRequestDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecurityException;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.websocket.AuthenticationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.security.Key;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Slf4j
@Service
public class TokenProvider {

    @Value("${jwt.secretKey}")
    private String SECRET_KEY ;
    private Key key;

    public String create(UserAccount userAccount){

        Date now = new Date();
        // 유효시간 1시간으로 처리
        Date expiredAt = Date.from(LocalDateTime.now().plusHours(1).atZone(ZoneId.systemDefault()).toInstant());
        key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());

        return Jwts.builder()
                .setSubject(userAccount.getUserId())
                .setIssuer("blind dating web")
                .setIssuedAt(now)
                .setExpiration(expiredAt)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public String refreshToken(UserAccount userAccount){

        Date now = new Date();
        Date expiredAt = Date.from(LocalDateTime.now().plusDays(7).atZone(ZoneId.systemDefault()).toInstant());
        key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());

        return Jwts.builder()
                .setIssuer("blind dating web")
                .setIssuedAt(now)
                .setExpiration(expiredAt)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public String validateAndGetUserId(String token, HttpServletRequest request) {
        key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
        Jwt jwt = null;

        try{
            jwt = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parse(token);
            Claims claims = (Claims)jwt.getBody();

            return claims.get("sub", String.class);
        }catch (SecurityException | MalformedJwtException e){
            request.setAttribute("exception","Invalid JWT Signature");
            throw new RuntimeException("Invalid JWT Signature");
        }catch (ExpiredJwtException e){
            request.setAttribute("exception","Expired JWT");
            throw new RuntimeException("Expired JWT");
        }catch (UnsupportedJwtException e){
            request.setAttribute("exception","Unsupported JWT");
            throw new RuntimeException("Unsupported JWT");
        }catch (IllegalArgumentException e){
            request.setAttribute("exception","JWT claims is Empty");
            throw new RuntimeException("JWT claims is Empty");
        }




    }

    public Boolean validateToken(String refreshToken) throws AuthenticationException {
        try {
            key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());

            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(refreshToken)
                    .getBody();
            return true;
        }catch (SecurityException | MalformedJwtException e){
            throw new AuthenticationException("Invalid JWT Signature");
        }catch (ExpiredJwtException e){
            throw new AuthenticationException("Expired JWT");
        }catch (UnsupportedJwtException e){
            throw new AuthenticationException("Unsupported JWT");
        }catch (IllegalArgumentException e){
            throw new AuthenticationException("JWT claims is Empty");
        }
    }
}
