package com.blind.dating.domain.chatRoom;

import com.blind.dating.domain.BaseEntity;
import com.blind.dating.domain.user.UserAccount;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.Objects;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@ToString
@Entity
public class ChatRoom extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    private Boolean status;

    @Setter
    private String recentMessage;

    public ChatRoom(Boolean status, String recentMessage) {
        this.status = status;
        this.recentMessage = recentMessage;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ChatRoom)) return false;
        ChatRoom chatRoom = (ChatRoom) o;
        return Objects.equals(id, chatRoom.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
