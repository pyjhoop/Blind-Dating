package com.blind.dating.handler;

import com.blind.dating.dto.user.UserSession;
import org.springframework.stereotype.Component;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class SessionHandler {
    private final ConcurrentHashMap<String, ConcurrentHashMap<String, String>> sessions = new ConcurrentHashMap<>();


    public void addSession(UserSession userSession) {
        String roomId = userSession.getRoomId();
        sessions.putIfAbsent(roomId, new ConcurrentHashMap<>());
        sessions.get(roomId).put(userSession.getUserId(), userSession.getUserId());
    }

    public void removeSession(String roomId, String userId) {

        ConcurrentHashMap<String, String> userSessions = sessions.get(roomId);
        if(userSessions != null){
            userSessions.remove(userId);
            if(userSessions.isEmpty()){
                sessions.remove(roomId);
            }
        }
    }

    public ConcurrentHashMap<String, String> getUsers(String roomId){

        return sessions.get(roomId);
    }

}