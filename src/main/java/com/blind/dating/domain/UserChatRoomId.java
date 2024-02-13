package com.blind.dating.domain;

import java.io.Serializable;
import java.util.Objects;

public class UserChatRoomId implements Serializable {

    private Long userAccount;
    private Long chatRoom;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserChatRoomId that = (UserChatRoomId) o;
        return Objects.equals(userAccount, that.userAccount) && Objects.equals(chatRoom, that.chatRoom);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userAccount, chatRoom);
    }
}
