package com.blind.dating.config.socket;

import org.springframework.stereotype.Component;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class SessionManager {
    private final ConcurrentHashMap<String, ConcurrentHashMap<String, String>> sessions = new ConcurrentHashMap<>();


    public void addSession(String userId, String roomId) {
        sessions.putIfAbsent(roomId, new ConcurrentHashMap<>());
        sessions.get(roomId).put(userId, userId);
    }

    public void removeSession(String userId, String roomId) {
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