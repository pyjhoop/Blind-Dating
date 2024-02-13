package com.blind.dating.service;

import com.blind.dating.common.code.MessageResponseCode;
import com.blind.dating.common.code.UserResponseCode;
import com.blind.dating.domain.ChatRoom;
import com.blind.dating.domain.Message;
import com.blind.dating.domain.UserAccount;
import com.blind.dating.domain.UserChatRoom;
import com.blind.dating.dto.message.MessageRequestDto;
import com.blind.dating.dto.message.MessageResponseDto;
import com.blind.dating.dto.message.MessageStatus;
import com.blind.dating.exception.ApiException;
import com.blind.dating.repository.ChattingRoomRepository;
import com.blind.dating.repository.MessageRepository;
import com.blind.dating.repository.UserAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor
@Service
public class MessageService {

    private final UserAccountRepository userAccountRepository;
    private final MessageRepository messageRepository;
    private final ChattingRoomRepository chattingRoomRepository;
    @Transactional
    public void postMessage(Long userId, MessageRequestDto dto) {
        UserAccount user = userAccountRepository.findById(userId)
                .orElseThrow(()-> new ApiException(MessageResponseCode.POST_MESSAGE_FAIL_WITH_USER_ID));

        messageRepository.save(dto.toEntity(user));
    }

    @Transactional
    public void acceptMessage(Long userId, Long messageId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(()-> new ApiException(MessageResponseCode.MESSAGE_NOT_FOUNT));

        if(Objects.equals(message.getReceiverId(), userId)) message.setStatus(MessageStatus.ACCEPT);
        else throw new ApiException(UserResponseCode.AUTHORIZE_FAIL);

        // 채팅룸 생성
        UserAccount receiver = userAccountRepository.findById(message.getReceiverId())
                .orElseThrow(()->new ApiException(MessageResponseCode.MESSAGE_NOT_FOUNT));

        ChatRoom chatRoom = new ChatRoom(false, "채팅방이 생성되었습니다.");
        ChatRoom chatRoomEntity = chattingRoomRepository.save(chatRoom);


        UserChatRoom userChatRoom1 = new UserChatRoom(message.getUserAccount(), chatRoomEntity);
        UserChatRoom userChatRoom2 = new UserChatRoom(receiver, chatRoomEntity);
        chatRoomEntity.setUserChatRooms(List.of(userChatRoom1, userChatRoom2));
    }

    @Transactional
    public void rejectMessage(Long userId, Long messageId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(()-> new ApiException(MessageResponseCode.MESSAGE_NOT_FOUNT));

        if(Objects.equals(message.getReceiverId(), userId)) message.setStatus(MessageStatus.REJECT);
        else throw new ApiException(UserResponseCode.AUTHORIZE_FAIL);
    }

    @Transactional(readOnly = true)
    public List<MessageResponseDto> getMessageToMe(Long userId) {
        return messageRepository.findAllByReceiverId(userId)
                .stream().map(MessageResponseDto::From).toList();
    }

    @Transactional(readOnly = true)
    public List<MessageResponseDto> getMessageFromMe(Long userId) {
        UserAccount user = userAccountRepository.findById(userId)
                .orElseThrow(()-> new ApiException(UserResponseCode.USER_NOT_FOUND));
        return messageRepository.findAllByUserAccount(user)
                .stream().map(MessageResponseDto::From).toList();
    }
}
