package com.blind.dating.domain.message;

import com.blind.dating.common.code.MessageResponseCode;
import com.blind.dating.common.code.UserResponseCode;
import com.blind.dating.domain.chatRoom.ChatRoom;
import com.blind.dating.domain.user.UserAccount;
import com.blind.dating.domain.userChatRoom.UserChatRoom;
import com.blind.dating.dto.message.MessageRequestDto;
import com.blind.dating.dto.message.MessageResponseDto;
import com.blind.dating.dto.message.MessageStatus;
import com.blind.dating.exception.ApiException;
import com.blind.dating.domain.chatRoom.ChattingRoomRepository;
import com.blind.dating.domain.user.UserAccountRepository;
import com.blind.dating.domain.userChatRoom.UserChatRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class MessageService {

    private final UserAccountRepository userAccountRepository;
    private final MessageRepository messageRepository;
    private final ChattingRoomRepository chattingRoomRepository;
    private final UserChatRoomRepository userChatRoomRepository;
    @Transactional
    public void postMessage(Authentication authentication, MessageRequestDto dto) {
        Long userId = Long.valueOf((String) authentication.getPrincipal());
        // 먼저 내가 본낸게 있으면 그걸 리턴시켜주자.
        UserAccount user = userAccountRepository.findById(userId)
                .orElseThrow(()-> new ApiException(MessageResponseCode.POST_MESSAGE_FAIL_WITH_USER_ID));

        Optional<Message> message = messageRepository.findByUserAccountAndReceiverId(user, dto.getReceiverId());
        if(message.isPresent()) throw new ApiException(MessageResponseCode.MESSAGE_ALREADY_POST);

        messageRepository.save(dto.toEntity(user));
    }

    @Transactional
    public void acceptMessage(Authentication authentication, Long messageId) {
        Long userId = Long.valueOf((String) authentication.getPrincipal());
        Message message = messageRepository.findByIdAndStatus(messageId, MessageStatus.WAIT)
                .orElseThrow(()-> new ApiException(MessageResponseCode.MESSAGE_NOT_FOUNT));

        if(Objects.equals(message.getReceiverId(), userId)) message.setStatus(MessageStatus.ACCEPT);
        else throw new ApiException(UserResponseCode.AUTHORIZE_FAIL);

        // 채팅룸 생성
        UserAccount receiver = userAccountRepository.findById(message.getReceiverId())
                .orElseThrow(()->new ApiException(MessageResponseCode.MESSAGE_NOT_FOUNT));

        ChatRoom chatRoom = new ChatRoom(receiver,true, "채팅방이 생성되었습니다.");
        ChatRoom chatRoomEntity = chattingRoomRepository.save(chatRoom);


        UserChatRoom userChatRoom1 = new UserChatRoom(message.getUserAccount(), chatRoomEntity);
        UserChatRoom userChatRoom2 = new UserChatRoom(receiver, chatRoomEntity);
        userChatRoomRepository.saveAll(List.of(userChatRoom1, userChatRoom2));
    }

    @Transactional
    public void rejectMessage(Authentication authentication, Long messageId) {
        Long userId = Long.valueOf((String) authentication.getPrincipal());
        Message message = messageRepository.findById(messageId)
                .orElseThrow(()-> new ApiException(MessageResponseCode.MESSAGE_NOT_FOUNT));

        if(Objects.equals(message.getReceiverId(), userId)) message.setStatus(MessageStatus.REJECT);
        else throw new ApiException(UserResponseCode.AUTHORIZE_FAIL);
    }

    @Transactional(readOnly = true)
    public List<MessageResponseDto> getMessageToMe(Authentication authentication) {
        Long userId = Long.valueOf((String) authentication.getPrincipal());
        return messageRepository.findAllByReceiverIdAndStatus(userId, MessageStatus.WAIT)
                .stream().map(MessageResponseDto::From).toList();
    }

    @Transactional(readOnly = true)
    public List<MessageResponseDto> getMessageFromMe(Authentication authentication) {
        Long userId = Long.valueOf((String) authentication.getPrincipal());

        UserAccount user = userAccountRepository.findById(userId)
                .orElseThrow(()-> new ApiException(UserResponseCode.USER_NOT_FOUND));
        return messageRepository.findAllByUserAccount(user)
                .stream().map(MessageResponseDto::From).toList();
    }
}