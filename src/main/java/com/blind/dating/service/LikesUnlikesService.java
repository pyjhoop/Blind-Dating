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
    public ResponseDto<LikesUnlikes> likeUser(Authentication authentication, String receiverId){
        UserAccount userAccount = (UserAccount)authentication.getPrincipal();
        Long receiver = Long.parseLong(receiverId);
        boolean bol = false;

        // receiverId로 유저가 존재하는지 조회
        Optional<UserAccount> result = userAccountRepository.findById(receiver);
        UserAccount receiverAccount = null;
        // receiverId에 해당하는 유저 존재유무에 따른 예외 처리
        if(result.isPresent()){
            receiverAccount = result.get();
        }else{
            throw new RuntimeException("존재하지 않는 계정입니다.");
        }

        // receiverId를 가진 유저가 나를 이미 좋아요 눌렀는지 확인 후 좋아요 눌렀으면 채팅 방 생성하기.
        List<LikesUnlikes> list = likesUnlikesRepositoryImpl.findLikes(receiverAccount.getId(), userAccount);

        //ReadChat도 생성해야함.
        ChatRoom chatRoom = null;
        if(list.isEmpty()){
            bol = false;
        }else {
            chatRoom = chatRoomService.create(userAccount, receiverAccount);
            // 여기서 ReadChat 생성
            readChatRepository.save(ReadChat.of(chatRoom.getId(),userAccount.getId(),0L));
            readChatRepository.save(ReadChat.of(chatRoom.getId(),receiverAccount.getId(),0L));
            bol = true;
        }


        // 해당 유저가 존재하면 내가 이전에 이미 좋아요 또는 싫어요를 했는지 확인 후 처리
        Optional<LikesUnlikes> likesUnlikes = likesUnlikesRepository.findByUserIdAndReceiverId(userAccount.getId(), receiver);
        LikesUnlikes result1 = null;
        if(likesUnlikes.isEmpty()){
            LikesUnlikes like = LikesUnlikes.of(userAccount.getId(), receiverAccount, true);

            result1 = likesUnlikesRepository.save(like);
            return ResponseDto.<LikesUnlikes>builder()
                    .status("OK")
                    .message(String.valueOf(bol))
                    .data(result1).build();
        }else{
            // 존재하는데
            // 1. 이미 true인 경우 -> null로 변경
            // 2. 이미 false인 경우 -> true로 변경
            if(likesUnlikes.get().getIsLike() == null){
                likesUnlikes.get().setIsLike(true);
            }else if(likesUnlikes.get().getIsLike()){
                likesUnlikes.get().setIsLike(null);
            }else{
                likesUnlikes.get().setIsLike(true);
            }

            return ResponseDto.<LikesUnlikes>builder()
                    .status("OK")
                    .message(String.valueOf(bol))
                    .data(likesUnlikes.get()).build();
        }
    }

    @Transactional
    public LikesUnlikes unlikeUser(Authentication authentication, String receiverId){
        UserAccount userAccount = (UserAccount)authentication.getPrincipal();
        Long receiver = Long.parseLong(receiverId);

        // receiverId로 유저 조회
        UserAccount receiverAccount = userAccountRepository.findById(receiver).get();

        // 먼저 상태가 어떤지 확인
        Optional<LikesUnlikes> likesUnlikes = likesUnlikesRepository.findByUserIdAndReceiverId(userAccount.getId(), receiver);
        if(likesUnlikes.isEmpty()){
            LikesUnlikes like = LikesUnlikes.of(userAccount.getId(), receiverAccount, false);

            LikesUnlikes result = likesUnlikesRepository.save(like);
            return result;
        }else{
            // 존재하는데
            // 1. 이미 false인 경우 -> null로 변경
            // 2. 이미 true 경우 -> false 변경
            if(likesUnlikes.get().getIsLike() == null){
                likesUnlikes.get().setIsLike(false);
            }else if(likesUnlikes.get().getIsLike()){
                likesUnlikes.get().setIsLike(false);
            }else{
                likesUnlikes.get().setIsLike(null);
            }

            return likesUnlikes.get();
        }

    }
}
