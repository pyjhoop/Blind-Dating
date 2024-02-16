package com.blind.dating.domain.userChatRoom;

import com.blind.dating.domain.chatRoom.ChatRoom;
import com.blind.dating.domain.user.UserAccount;
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
