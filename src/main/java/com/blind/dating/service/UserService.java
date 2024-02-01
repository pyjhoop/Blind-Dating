package com.blind.dating.service;

import com.blind.dating.domain.Interest;
import com.blind.dating.domain.Question;
import com.blind.dating.domain.UserAccount;
import com.blind.dating.dto.user.UserIdWithNicknameAndGender;
import com.blind.dating.dto.user.UserUpdateRequestDto;
import com.blind.dating.dto.user.UserWithInterestAndQuestionDto;
import com.blind.dating.repository.InterestRepository;
import com.blind.dating.repository.UserAccountRedisRepository;
import com.blind.dating.repository.UserAccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
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
    private final InterestRepository interestRepository;
    private final UserAccountRedisRepository userAccountRedisRepository;

    /**
     * 이성 추천리스트 조회
     * @param authentication
     * @param pageable
     * @return Page<UserAccount>
     */
    public Page<UserWithInterestAndQuestionDto> getUserList(Authentication authentication, Pageable pageable){
        String userId = (String) authentication.getPrincipal();
        UserAccount user = userAccountRepository.findById(Long.valueOf(userId))
                .orElseThrow(() -> new RuntimeException("예외 발생"));
        Page<UserAccount> users;
        if(user.getGender().equals("M")){
            users = userAccountRepository.recommendUser("W",Long.valueOf(userId), pageable);
        }else{
            users = userAccountRepository.recommendUser("M",Long.valueOf(userId), pageable);
        }


        return users.map(UserWithInterestAndQuestionDto::from);

    }

    /**
     * 내 정보 조회하기
     * @param authentication
     * @return UserAccount
     */
    public UserWithInterestAndQuestionDto getMyInfo(Authentication authentication){
        String userId = (String) authentication.getPrincipal();

        UserAccount user = userAccountRepository.findById(Long.valueOf(userId))
                .orElseThrow(()-> new RuntimeException("내정보 조회에 실패했습니다."));

        return UserWithInterestAndQuestionDto.from(user);
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

        UserAccount user = userAccountRepository.findById(Long.valueOf(userId))
                .orElseThrow(() -> new RuntimeException("유저정보 조회중에 예외가 발생했습니다."));
        // 유저 정보 저장하기
        user.setRegion(dto.getRegion());
        user.setMbti(dto.getMbti());
        user.setSelfIntroduction(dto.getSelfIntroduction());

        interestRepository.deleteAllByUserAccount(user);

        List<Interest> interests = new ArrayList<>();
        for(String s: dto.getInterests()){
            interests.add(Interest.of(user,s));
        }
        user.setInterests(interests);

        return user;
    }

    public Optional<UserAccount> getUser(Long otherId) {
        return userAccountRepository.findById(otherId);
    }
}
