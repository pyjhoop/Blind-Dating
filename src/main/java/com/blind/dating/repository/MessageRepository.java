package com.blind.dating.repository;

import com.blind.dating.domain.Message;
import com.blind.dating.domain.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findAllByReceiverId(Long receiverId);
    List<Message> findAllByUserAccount(UserAccount user);
}
