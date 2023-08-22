package com.blind.dating.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Getter
@Setter
@ToString
@NoArgsConstructor
@Entity
public class ReadChat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long roomId;

    private Long userId;

    private Long chatId;

    private ReadChat(Long roomId, Long userId, Long chatId) {
        this.roomId = roomId;
        this.userId = userId;
        this.chatId = chatId;
    }

    public static ReadChat of(Long roomId, Long userId, Long chatId) {
        return new ReadChat(roomId, userId, chatId);
    }
}
