package com.blind.dating.service;

import com.blind.dating.domain.ChatRoom;
import com.blind.dating.domain.LikesUnlikes;
import com.blind.dating.domain.ReadChat;
import com.blind.dating.domain.UserAccount;
import com.blind.dating.dto.response.ResponseDto;
import com.blind.dating.repository.LikesUnlikesRepository;
import com.blind.dating.repository.ReadChatRepository;
import com.blind.dating.repository.UserAccountRepository;
import com.blind.dating.repository.querydsl.LikesUnlikesRepositoryImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LikesUnlikesService {

    private final LikesUnlikesRepository likesUnlikesRepository;
    private final UserAccountRepository userAccountRepository;
    private final LikesUnlikesRepositoryImpl likesUnlikesRepositoryImpl;
    private final ChattingRoomService chatRoomService;
    private final ReadChatRepository readChatRepository;

    @Transactional
    public Boolean likeUser(Authentication authentication, String receiverId){
        String userId = (String) authentication.getPrincipal();
        Long receiver = Long.parseLong(receiverId);

        // receiverId로 유저가 존재하는지 조회
        Optional<UserAccount> result = userAccountRepository.findById(receiver);
        UserAccount receiverAccount = null;
        // receiverId에 해당하는 유저 존재유무에 따른 예외 처리
        if(result.isPresent()){
            receiverAccount = result.get();
        }else{
            throw new RuntimeException("존재하지 않는 계정입니다.");
        }

        // like 저장
        likesUnlikesRepository.save(LikesUnlikes.of(Long.valueOf(userId),receiverAccount, true));

        Optional<UserAccount> userAccount = userAccountRepository.findById(Long.valueOf(userId));

        // receiverId를 가진 유저가 나를 이미 좋아요 눌렀는지 확인 후 좋아요 눌렀으면 채팅 방 생성하기.
        List<LikesUnlikes> list = likesUnlikesRepositoryImpl.findLikes(receiverAccount.getId(), userAccount.get());

        //ReadChat도 생성해야함.
        ChatRoom chatRoom = null;
        if(list.isEmpty()){
            return false;
        }else {
            chatRoom = chatRoomService.create(userAccount.get(), receiverAccount);
            chatRoom.setStatus(true);
            // 여기서 ReadChat 생성
            readChatRepository.save(ReadChat.of(chatRoom,userAccount.get().getId(),0L));
            readChatRepository.save(ReadChat.of(chatRoom,receiverAccount.getId(),0L));
            return true;
        }

    }

    @Transactional
    public void unlikeUser(Authentication authentication, String receiverId){
        String userId = (String) authentication.getPrincipal();
        Long receiver = Long.parseLong(receiverId);

        // receiverId로 유저 조회
        UserAccount receiverAccount = userAccountRepository.findById(receiver).get();

        likesUnlikesRepository.save(LikesUnlikes.of(Long.valueOf(userId), receiverAccount, false));

    }
}
