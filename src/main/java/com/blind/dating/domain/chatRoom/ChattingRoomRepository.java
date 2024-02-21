package com.blind.dating.domain.chatRoom;

import com.blind.dating.domain.chatRoom.ChatRoom;
import com.blind.dating.domain.user.UserAccount;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ChattingRoomRepository extends JpaRepository<ChatRoom, Long> {
    @Query("SELECT cua.chatRoom FROM ChatRoomUserAccount cua WHERE cua.userAccount.id = :userId ORDER BY cua.chatRoom.updatedAt DESC")
    List<ChatRoom> findChatRoomsByUserIdOrderByUpdatedDateDesc(@Param("userId") Long userId);

}
