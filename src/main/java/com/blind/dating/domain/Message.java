package com.blind.dating.domain;

import com.blind.dating.dto.message.MessageStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Message {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private UserAccount userAccount;

    @Column(nullable = false)
    private Long receiverId;

    @Column(nullable = false, length = 3000)
    private String content;

    @Enumerated(EnumType.STRING) @Setter
    private MessageStatus status;

    private Message(UserAccount userAccount, Long receiverId, String content, MessageStatus status) {
        this.userAccount = userAccount;
        this.receiverId = receiverId;
        this.content = content;
        this.status = status;
    }

    public static Message of(UserAccount userAccount, Long receiverId, String content, MessageStatus status) {
        return new Message(userAccount, receiverId, content, status);
    }
}
