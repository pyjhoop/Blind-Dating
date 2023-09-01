package com.blind.dating.security;

import com.blind.dating.domain.UserAccount;
import com.blind.dating.dto.user.UserRequestDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Slf4j
@Service
public class TokenProvider {

    private static final String SECRET_KEY = "c2lsdmVybmluZS10ZWNoLXNwcmluZy1ib290LWp3dC10dXRvcmlhbC1zZWNyZXQtc2lsdmVybmluZS10ZWNoLXNwcmluZy1ib290LWp3dC10dXRvcmlhbC1zZWNyZXQK";
    private Key key;

    public String create(String userId){

        Date now = new Date();
        // 유효시간 1시간으로 처리
        Date expiredAt = Date.from(LocalDateTime.now().plusHours(1).atZone(ZoneId.systemDefault()).toInstant());
        key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());

        return Jwts.builder()
                .setSubject(String.valueOf(userId))
                .setIssuer("blind dating web")
                .setIssuedAt(now)
                .setExpiration(expiredAt)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public String refreshToken(String userId){

        Date now = new Date();
        Date expiredAt = Date.from(LocalDateTime.now().plusDays(7).atZone(ZoneId.systemDefault()).toInstant());
        key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());

        return Jwts.builder()
                .setSubject(String.valueOf(userId))
                .setIssuer("blind dating web")
                .setIssuedAt(now)
                .setExpiration(expiredAt)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public String validateAndGetUserId(String token) {
        key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());

        Jwt jwt = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parse(token);


        Claims claims = (Claims)jwt.getBody();

        return claims.get("sub", String.class);
    }

    public Boolean validateToken(String token){
        try {
            key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());

            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            return true;
        }catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            log.info("Invalid JWT Token", e);
        } catch (ExpiredJwtException e) {
            log.info("Expired JWT Token", e);
        } catch (UnsupportedJwtException e) {
            log.info("Unsupported JWT Token", e);
        } catch (IllegalArgumentException e) {
            log.info("JWT claims string is empty.", e);
        }
        return false;
    }

    public Authentication getAuthentication(String token){
        key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());

        Jwt jwt = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parse(token);
        Claims claims = (Claims)jwt.getBody();

        String userId = claims.get("sub", String.class);

        return new UsernamePasswordAuthenticationToken(userId,"",null);

    }

}



