package com.blind.dating.service;

import com.blind.dating.domain.Message;
import com.blind.dating.domain.UserAccount;
import com.blind.dating.dto.MessageDto;
import com.blind.dating.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class MessageService {

    private final MessageRepository messageRepository;

    public List<Message> getMessages(Long receiverId){

        return messageRepository.findAllByReceiver(receiverId);

    }

    public List<Message> getSentMessages(UserAccount userAccount){

        return messageRepository.findAllBySender(userAccount);

    }

    @Transactional
    public Message getMessage(Long id){
         Message message = messageRepository.findById(id).get();
         message.setStatus("READ");
         return message;
    }

    public Message createMessage(MessageDto dto){
        dto.setStatus("UNREAD");
        return messageRepository.save(dto.toEntity());
    }
}
