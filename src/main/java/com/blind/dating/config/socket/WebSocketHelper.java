package com.blind.dating.config.socket;

import org.springframework.web.socket.WebSocketSession;

public class WebSocketHelper {
    public static String userIdKey = "userId";
    public static String roomIdKey = "roomId";

    public static String getUserIdFromSessionAttribute(WebSocketSession webSocketSession) {
        return (String) webSocketSession.getAttributes().get(userIdKey);
    }

    public static String getRoomIdFromSessionAttribute(WebSocketSession webSocketSession) {
        return (String) webSocketSession.getAttributes().get(roomIdKey);
    }

    public static String getUserIdFromUrl(String path){
        return path.substring(path.lastIndexOf('/') + 1);
    }

    public static String getRoomIdFromUrl(String path){
        return path.substring(path.indexOf('/',2) + 1,path.indexOf('/',2) + 2);
    }
}