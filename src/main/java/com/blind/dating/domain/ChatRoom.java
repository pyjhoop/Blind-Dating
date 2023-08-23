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
    @OneToOne
    private UserAccount user1;

    @Setter
    @OneToOne
    private UserAccount user2;

    @Setter
    private String recentMessage;

    @OneToMany(mappedBy = "chatRoom", cascade = CascadeType.ALL)
    private final Set<Chat> chats = new LinkedHashSet<>();

    public ChatRoom(UserAccount user1, UserAccount user2) {
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
