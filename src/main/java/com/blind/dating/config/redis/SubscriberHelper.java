package com.blind.dating.config.redis;

import com.blind.dating.config.socket.WebSocketSessionManager;
import io.lettuce.core.pubsub.RedisPubSubListener;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.TextMessage;

import java.io.IOException;

@RequiredArgsConstructor
@Slf4j
public class SubscriberHelper implements RedisPubSubListener<String, String> {
    private final WebSocketSessionManager webSocketSessionManager;

    @Override
    public void message(String channel, String message) {
        log.info("got the message on redis "+ channel+ " and "+ message);
        var ws = this.webSocketSessionManager.getWebSocketSessions(channel);
        try {
            ws.sendMessage(new TextMessage(message));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void message(String s, String k1, String s2) {

    }

    @Override
    public void subscribed(String s, long l) {

    }

    @Override
    public void psubscribed(String s, long l) {

    }

    @Override
    public void unsubscribed(String s, long l) {

    }

    @Override
    public void punsubscribed(String s, long l) {

    }
}