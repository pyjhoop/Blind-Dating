package com.blind.dating.repository;

import com.blind.dating.domain.Chat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatRepository extends JpaRepository<Chat,Long> {

    List<Chat> findAllByChatRoomIdOrderByIdAsc(Long roomId);
}
