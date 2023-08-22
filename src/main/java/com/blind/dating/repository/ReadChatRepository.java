package com.blind.dating.repository;

import com.blind.dating.domain.ReadChat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReadChatRepository extends JpaRepository<ReadChat, Long> {

    Optional<ReadChat> findByRoomIdAndUserId(Long roomId, Long userId);
}
