package com.blind.dating.service;

import com.blind.dating.domain.LikesUnlikes;
import com.blind.dating.domain.UserAccount;
import com.blind.dating.repository.LikesUnlikesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LikesUnlikesService {

    private final LikesUnlikesRepository likesUnlikesRepository;

    @Transactional
    public LikesUnlikes likeUser(Authentication authentication, String receiverId){
        UserAccount userAccount = (UserAccount)authentication.getPrincipal();
        Long receiver = Long.parseLong(receiverId);

        // 먼저 상태가 어떤지 확인
        Optional<LikesUnlikes> likesUnlikes = likesUnlikesRepository.findByUserAccountAndReceiverId(userAccount, receiver);
        if(likesUnlikes.isEmpty()){
            LikesUnlikes like = LikesUnlikes.of(userAccount, receiver, true);

            LikesUnlikes result = likesUnlikesRepository.save(like);
            return result;
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

            return likesUnlikes.get();
        }

    }

    @Transactional
    public LikesUnlikes unlikeUser(Authentication authentication, String receiverId){
        UserAccount userAccount = (UserAccount)authentication.getPrincipal();
        Long receiver = Long.parseLong(receiverId);

        // 먼저 상태가 어떤지 확인
        Optional<LikesUnlikes> likesUnlikes = likesUnlikesRepository.findByUserAccountAndReceiverId(userAccount, receiver);
        if(likesUnlikes.isEmpty()){
            LikesUnlikes like = LikesUnlikes.of(userAccount, receiver, false);

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
