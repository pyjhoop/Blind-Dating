package com.blind.dating.config.redis;

import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Getter
public class RedisConnection {
    private final RedisClient client;
    private final StatefulRedisConnection<String, String> connection;

    private RedisConnection(@Value("${spring.data.redis.host}") String host) {
        this.client = RedisClient.create("redis://"+host+":6379");
        this.connection = client.connect();
    }

}
