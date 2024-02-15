package com.blind.dating.repository;

import com.blind.dating.domain.Chat;
import com.blind.dating.domain.ChatRoom;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ChatRepository extends JpaRepository<Chat,Long> {

    Page<Chat> findAllByChatRoom(ChatRoom chatRoom, Pageable pageable);

    Long countByIdBetween(Long chatId1, Long chatId2);

    Optional<Chat> findFirstByChatRoomIdOrderByCreatedAtDesc(Long roomId);


    List<Chat> findByChatRoomAndIdLessThanEqualOrderByIdDesc(ChatRoom chatRoom, Long chatId);


}
