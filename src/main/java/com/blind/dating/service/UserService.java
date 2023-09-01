package com.blind.dating.service;

import com.blind.dating.domain.Interest;
import com.blind.dating.domain.UserAccount;
import com.blind.dating.dto.user.UserIdWithNickname;
import com.blind.dating.dto.user.UserUpdateRequestDto;
import com.blind.dating.repository.InterestRepository;
import com.blind.dating.repository.UserAccountRedisRepository;
import com.blind.dating.repository.UserAccountRepository;
import com.blind.dating.repository.querydsl.UserAccountRepositoryImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
@Slf4j
public class UserService {

    private final UserAccountRepository userAccountRepository;
    private final UserAccountRepositoryImpl userAccountRepositoryImpl;
    private final InterestRepository interestRepository;
    private final UserAccountRedisRepository userAccountRedisRepository;

    /**
     * 이성 추천리스트 조회
     * @param authentication
     * @param pageable
     * @return Page<UserAccount>
     */
    public Page<UserAccount> getUserList(Authentication authentication, Pageable pageable){
        String userId =  (String)authentication.getPrincipal();
        UserIdWithNickname userInfo = userAccountRedisRepository.getUser(userId);

        if(userInfo.getGender().equals("M")){
            return userAccountRepositoryImpl.findAllByGenderAndNotLikes("W",userInfo.getId(), pageable);
        }else{
            return userAccountRepositoryImpl.findAllByGenderAndNotLikes("M",userInfo.getId(), pageable);
        }

    }

    /**
     * 내 정보 조회하기
     * @param authentication
     * @return UserAccount
     */
    public UserAccount getMyInfo(Authentication authentication){
        String userId = (String) authentication.getPrincipal();

        Optional<UserAccount> user = userAccountRepository.findById(Long.parseLong(userId));

        if(user.isPresent()){
            return user.get();
        }else{
            throw new RuntimeException("내 정보를 찾을 수 없습니다. 다시 조회해 주세요");
        }


    }

    /**
     * 내 정보 수정하기.
     * @param authentication
     * @param dto
     * @return UserAccount
     */
    @Transactional
    public UserAccount updateMyInfo(Authentication authentication, UserUpdateRequestDto dto){

        String userId = (String) authentication.getPrincipal();

        Optional<UserAccount> user = userAccountRepository.findById(Long.parseLong(userId));

        if(user.isPresent()){
            // 유저 정보 수정
            user.get().setRegion(dto.getRegion());
            user.get().setMbti(dto.getMbti());
            user.get().setSelfIntroduction(dto.getSelfIntroduction());

            //
            interestRepository.deleteAllByUserAccount(user.get());
            List<Interest> list = new ArrayList<>();
            for(String s: dto.getInterests()){
                list.add(Interest.of(user.get(),s));
            }
            interestRepository.saveAll(list);
            return user.get();
        }else {
            throw new RuntimeException("정보 수정하는데 에러가 발생했습니다. 다시 요청해주세요");
        }


    }

    public Optional<UserAccount> getUser(Long otherId) {
        return userAccountRepository.findById(otherId);
    }
}
