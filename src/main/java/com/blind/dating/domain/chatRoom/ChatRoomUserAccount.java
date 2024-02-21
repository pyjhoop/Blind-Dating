package com.blind.dating.domain.chatRoom;

import com.blind.dating.domain.user.UserAccount;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor
public class ChatRoomUserAccount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "userAccountId")
    private UserAccount userAccount;

    @ManyToOne
    @JoinColumn(name = "chatRoomId")
    private ChatRoom chatRoom;

    public ChatRoomUserAccount(UserAccount userAccount, ChatRoom chatRoom) {
        this.userAccount = userAccount;
        this.chatRoom = chatRoom;
    }
}