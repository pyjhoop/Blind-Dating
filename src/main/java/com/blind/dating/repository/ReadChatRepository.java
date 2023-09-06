package com.blind.dating.repository;

import com.blind.dating.domain.ChatRoom;
import com.blind.dating.domain.ReadChat;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.swing.text.html.Option;
import java.util.Optional;

public interface ReadChatRepository extends JpaRepository<ReadChat, Long> {

    Optional<ReadChat> findByChatRoomAndUserId(ChatRoom chatRoom, Long userId);


}
