package com.blind.dating.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;

@Getter
@NoArgsConstructor
@Entity
public class Chat extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Setter
    private Long chatRoomId;
    @Setter
    private Long writerId;
    @Setter
    private String message;

    private Chat(Long chatRoomId, Long writerId, String message) {
        this.chatRoomId = chatRoomId;
        this.writerId = writerId;
        this.message = message;
    }

    public static Chat of(Long chatRoomId, Long writerId, String message) {
        return new Chat(chatRoomId, writerId, message);
    }


}
