package com.blind.dating.domain;

import jakarta.persistence.*;
import lombok.*;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@ToString
@Entity
public class ChatRoom extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chatRoomId")
    private Long id;

    @OneToMany(mappedBy = "chatRoom", cascade = CascadeType.ALL)
    @Setter
    private List<UserChatRoom> userChatRooms;

    @Setter
    @OneToMany(mappedBy = "chatRoom")
    @ToString.Exclude
    private Set<ReadChat> readChat = new LinkedHashSet<>();

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
