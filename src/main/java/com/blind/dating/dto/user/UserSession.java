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

    public UserSession(String userName, String userId, String sessionId, String roomId) {
        this.userName = userName;
        this.userId = userId;
        this.sessionId = sessionId;
        this.roomId = roomId;
    }
}
