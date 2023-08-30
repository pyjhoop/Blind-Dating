package com.blind.dating.repository;

import com.blind.dating.domain.ChatRoom;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ChattingRoomRepository extends JpaRepository<ChatRoom, Long> {
    List<ChatRoom> findAllByUser1OrUser2AndLeaveIdNot(Long user1, Long user2, Long leaveId);

    @Query("SELECT c FROM ChatRoom c WHERE (c.user1 = :user1 OR c.user2 = :user2) AND (c.leaveId <> :leaveId OR c.leaveId is null) order by c.updatedAt DESC ")
    List<ChatRoom> findCustomQuery(@Param("user1") Long user1, @Param("user2") Long user2, @Param("leaveId") Long leaveId);

}
