package com.blind.dating.repository;

import com.blind.dating.domain.Chat;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ChatRepository extends JpaRepository<Chat,Long> {

    List<Chat> findAllByChatRoomIdOrderByIdAsc(Long roomId);
    List<Chat> findAllByChatRoomIdOrderByIdDesc(Long roomId);

    Long countByIdBetween(Long chatId1, Long chatId2);

    Optional<Chat> findFirstByChatRoomIdOrderByCreatedAtDesc(Long roomId);

    Page<Chat> findAllByChatRoomId(Long roomId, Pageable pageable);


}
