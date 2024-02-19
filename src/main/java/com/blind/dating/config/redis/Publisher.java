package com.blind.dating.config.redis;

import io.lettuce.core.RedisClient;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class Publisher {

    RedisClient client;


    public Publisher(@Value("${spring.data.redis.host}") String host){
        this.client = RedisClient.create("redis://"+host+":6379");
    }

    public void publish(String channel, String message){
        log.info("going to publish the message to channel {} and message = {}", channel, message);
        var connection = this.client.connect();
        connection.sync().publish(channel,message);
    }

}
