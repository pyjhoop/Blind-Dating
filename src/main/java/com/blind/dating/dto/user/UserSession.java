package com.blind.dating.dto.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@NoArgsConstructor
public class UserSession {
    private String userName;
    private String userId;
    private String sessionId;
    private String roomId;

    private UserSession(String userName, String userId, String sessionId, String roomId) {
        this.userName = userName;
        this.userId = userId;
        this.sessionId = sessionId;
        this.roomId = roomId;
    }

    public static UserSession of(String userName, String userId, String sessionId, String roomId) {
        return new UserSession(userName, userId, sessionId, roomId);
    }
}
