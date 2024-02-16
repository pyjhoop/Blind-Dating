package com.blind.dating.domain.chatRoom;

import com.blind.dating.domain.chatRoom.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChattingRoomRepository extends JpaRepository<ChatRoom, Long> {

//    List<ChatRoom> findAllByUsersAndStatusOrderByUpdatedAtDesc(UserAccount userAccount, Boolean status);
}
