package com.blind.dating.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.util.Objects;

@Getter
@ToString
@Table
@Entity
public class Message extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private UserAccount sender;

    @Column(nullable = false)
    private Long receiver;

    @Setter
    @Column(nullable = false)
    private String messageContent;

    @Setter
    @Column(nullable = false)
    private String status;

    protected Message(){}

    private Message(UserAccount sender, Long receiver, String messageContent, String status) {
        this.sender = sender;
        this.receiver = receiver;
        this.messageContent = messageContent;
        this.status = status;
    }

    public static Message of(UserAccount sender, Long receiver, String messageContent, String status){
        return new Message(sender, receiver, messageContent, status);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Message)) return false;
        Message message = (Message) o;
        return Objects.equals(id, message.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
