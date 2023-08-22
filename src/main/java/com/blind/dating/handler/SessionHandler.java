package com.blind.dating.handler;

import com.blind.dating.dto.user.UserSession;
import org.springframework.stereotype.Component;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class SessionHandler {
    private final ConcurrentHashMap<String, ConcurrentHashMap<String, UserSession>> sessions = new ConcurrentHashMap<>();


    public void addSession(UserSession userSession) {
        String roomId = userSession.getRoomId();
        sessions.putIfAbsent(roomId, new ConcurrentHashMap<>());
        sessions.get(roomId).put(userSession.getSessionId(), userSession);
    }

    public void removeSession(String roomId, String sessionId) {

        ConcurrentHashMap<String, UserSession> userSessions = sessions.get(roomId);
        if(userSessions != null){
            userSessions.remove(sessionId);
            if(userSessions.isEmpty()){
                sessions.remove(roomId);
            }
        }
    }

    public ConcurrentHashMap<String, UserSession> getUsers(String roomId){

        return sessions.get(roomId);
    }

}