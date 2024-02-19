package com.blind.dating.config.socket;

import com.blind.dating.config.socket.WebSocketHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class WebSocketSessionManager {
    private final Map<String, WebSocketSession> webSocketSessionByUserId = new HashMap<>();

    public void addWebSocketSession(WebSocketSession webSocketSession){
        String userId = WebSocketHelper.getUserIdFromSessionAttribute(webSocketSession);
        String roomId = WebSocketHelper.getRoomIdFromSessionAttribute(webSocketSession);
        String chanel = "room/"+roomId+"/user/"+userId;
        webSocketSessionByUserId.put(chanel,webSocketSession);
        log.info("added session id {} for chanel {}", webSocketSession.getId(), chanel);
    }

    public void removeWebSocketSession(WebSocketSession webSocketSession){
        String userId = WebSocketHelper.getUserIdFromSessionAttribute(webSocketSession);
        String roomId = WebSocketHelper.getRoomIdFromSessionAttribute(webSocketSession);
        String chanel = "room/"+roomId+"/user/"+userId;
        this.webSocketSessionByUserId.remove(userId);
        log.info("removed session id {} for chanel id {}", webSocketSession.getId(), chanel);
    }

    public WebSocketSession getWebSocketSessions(String userId){
        return this.webSocketSessionByUserId.get(userId);
    }
}
