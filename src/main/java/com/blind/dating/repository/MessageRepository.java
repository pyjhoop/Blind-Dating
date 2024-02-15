package com.blind.dating.repository;

import com.blind.dating.domain.Message;
import com.blind.dating.domain.UserAccount;
import com.blind.dating.dto.message.MessageStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findAllByReceiverIdAndStatus(Long receiverId, MessageStatus status);
    List<Message> findAllByUserAccount(UserAccount user);

    Optional<Message> findByUserAccountAndReceiverId(UserAccount user, Long receiverId);
    Optional<Message> findByIdAndStatus(Long id, MessageStatus status);
}
