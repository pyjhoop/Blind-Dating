package com.blind.dating.config.socket;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@NoArgsConstructor
public class UserSession {
    private String userId;
    private String roomId;

    private UserSession( String userId, String roomId) {
        this.userId = userId;
        this.roomId = roomId;
    }

    public static UserSession of(String userId, String roomId) {
        return new UserSession(userId, roomId);
    }
}
