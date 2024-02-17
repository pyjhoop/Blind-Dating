package com.blind.dating.domain.userChatRoom;

import com.blind.dating.domain.user.UserAccount;
import com.blind.dating.domain.userChatRoom.UserChatRoom;
import com.blind.dating.domain.userChatRoom.UserChatRoomId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserChatRoomRepository extends JpaRepository<UserChatRoom, UserChatRoomId> {
    List<UserChatRoom> findAllByUserAccount(UserAccount userAccount);
}
