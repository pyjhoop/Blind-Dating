package com.blind.dating.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@IdClass(UserChatRoomId.class)
public class UserChatRoom {

    @Id
    @ManyToOne
    @JoinColumn(name = "userAccountId")
    private UserAccount userAccount;

    @Id
    @ManyToOne
    @JoinColumn(name = "chatRoomId")
    private ChatRoom chatRoom;


}
