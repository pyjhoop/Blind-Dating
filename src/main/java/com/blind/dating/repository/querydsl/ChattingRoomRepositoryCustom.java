package com.blind.dating.repository.querydsl;

import com.blind.dating.domain.ChatRoom;
import com.blind.dating.domain.UserAccount;
import com.blind.dating.dto.chat.ChatRoomDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ChattingRoomRepositoryCustom {

    Page<ChatRoom> findAllByUserId(Long userId1, Long userId2, Pageable pageable);

}
