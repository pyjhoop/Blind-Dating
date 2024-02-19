package com.blind.dating.domain.token;

import com.blind.dating.config.redis.RedisConnection;
import io.lettuce.core.api.async.RedisAsyncCommands;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Slf4j
@Repository
public class RefreshTokenRepository {

    private final RedisConnection redisConnection;

    public void save(Long userId, String refreshToken){
        RedisAsyncCommands<String , String> asyncCommands = redisConnection.getConnection().async();

        asyncCommands.setex(userId.toString(),60*60*12 ,refreshToken).thenAccept(result -> {
            log.info("토큰 저장 완료: " + result);
            // 명령 실행 후 연결을 종료합니다.
        }).exceptionally(e -> {
            log.error("토큰 저장 실패", e);
            return null;
        });

    }

    public String getRefreshToken(String userId){
        return redisConnection.getConnection().sync().get(userId);
    }

    public void deleteRefreshToken(String userId){
        redisConnection.getConnection().async().del(userId);
    }

}
