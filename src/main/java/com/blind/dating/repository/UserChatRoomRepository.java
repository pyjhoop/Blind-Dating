package com.blind.dating.repository;

import com.blind.dating.domain.UserAccount;
import com.blind.dating.domain.UserChatRoom;
import com.blind.dating.domain.UserChatRoomId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserChatRoomRepository extends JpaRepository<UserChatRoom, UserChatRoomId> {
    List<UserChatRoom> findAllByUserAccount(UserAccount userAccount);
}
