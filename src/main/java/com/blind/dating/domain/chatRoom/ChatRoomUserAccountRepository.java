package com.blind.dating.domain.chatRoom;

import com.blind.dating.dto.chat.ChatRoomDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ChatRoomUserAccountRepository extends JpaRepository<ChatRoomUserAccount, Long> {

    @Query("SELECT new com.blind.dating.dto.chat.ChatRoomDto(cra.chatRoom.id, cra.chatRoom.updatedAt, ua.id, ua.nickname, cra.chatRoom.recentMessage)" +
            "FROM ChatRoomUserAccount cra " +
            "JOIN cra.chatRoom cr " +
            "JOIN cra.userAccount ua " +
            "WHERE cr.id = (SELECT crua.chatRoom.id FROM ChatRoomUserAccount crua WHERE crua.userAccount.id = :userId)" +
            "AND ua.id != :userId")
    List<ChatRoomDto> findChatRoomsAndUsersByUserId(Long userId);


    @Query("SELECT new com.blind.dating.dto.chat.ChatRoomDto(cra.chatRoom.id, cra.chatRoom.updatedAt, ua.id, ua.nickname, cra.chatRoom.recentMessage) " +
            "FROM ChatRoomUserAccount cra " +
            "JOIN cra.chatRoom cr " +
            "JOIN cra.userAccount ua " +
            "WHERE cra.chatRoom.id = :roomId " +
            "AND cra.userAccount.id != :userId"
    )
    Optional<ChatRoomDto> findChatRoomByUserIdAndRoomId(Long userId, Long roomId);
}
