package com.blind.dating.domain.readChat;

import com.blind.dating.domain.chatRoom.ChatRoom;
import com.blind.dating.domain.readChat.ReadChat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReadChatRepository extends JpaRepository<ReadChat, Long> {

    Optional<ReadChat> findByChatRoomAndUserId(ChatRoom chatRoom, Long userId);

    Optional<ReadChat> findByChatRoomIdAndUserId(Long chatRoomId, Long userId);

}
