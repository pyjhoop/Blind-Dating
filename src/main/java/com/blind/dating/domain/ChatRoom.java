package com.blind.dating.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

@NoArgsConstructor
@Getter
@ToString
@Entity
public class ChatRoom extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    private Long user1;

    @Setter
    private Long user2;

    @Setter
    private Long leaveId;

    @Setter
    private Boolean status;

    @Setter
    private String recentMessage;

    @ToString.Exclude
    @OneToMany(mappedBy = "chatRoom", cascade = CascadeType.ALL)
    private final Set<Chat> chats = new LinkedHashSet<>();

    public ChatRoom(Long user1, Long user2) {
        this.user1 = user1;
        this.user2 = user2;
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
