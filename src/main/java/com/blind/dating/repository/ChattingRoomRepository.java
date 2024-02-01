package com.blind.dating.repository;

import com.blind.dating.domain.ChatRoom;
import com.blind.dating.domain.UserAccount;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

public interface ChattingRoomRepository extends JpaRepository<ChatRoom, Long> {

    List<ChatRoom> findAllByUsersAndStatusOrderByUpdatedAtDesc(UserAccount userAccount, Boolean status);
}
