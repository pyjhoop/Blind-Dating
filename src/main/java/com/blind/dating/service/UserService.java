package com.blind.dating.service;

import com.blind.dating.domain.Interest;
import com.blind.dating.domain.UserAccount;
import com.blind.dating.dto.user.UserUpdateRequestDto;
import com.blind.dating.repository.InterestRepository;
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

    /**
     * 이성 추천리스트 조회
     * @param authentication
     * @param pageable
     * @return Page<UserAccount>
     */
    public Page<UserAccount> getUserList(Authentication authentication, Pageable pageable){
        UserAccount user = (UserAccount) authentication.getPrincipal();
        String gender = user.getGender();

        if(gender.equals("M")){
            return userAccountRepositoryImpl.findAllByGenderAndNotLikes("W",user.getId(), pageable);
        }else{
            return userAccountRepositoryImpl.findAllByGenderAndNotLikes("M",user.getId(), pageable);
        }

    }

    /**
     * 내 정보 조회하기
     * @param authentication
     * @return UserAccount
     */
    public UserAccount getMyInfo(Authentication authentication){
        UserAccount user = (UserAccount) authentication.getPrincipal();
        return userAccountRepository.findByUserId(user.getUserId());
    }

    /**
     * 내 정보 수정하기.
     * @param authentication
     * @param dto
     * @return UserAccount
     */
    @Transactional
    public UserAccount updateMyInfo(Authentication authentication, UserUpdateRequestDto dto){

        UserAccount user = ((UserAccount) authentication.getPrincipal());

        // 유저 정보 저장하기
        user.setRegion(dto.getRegion());
        user.setMbti(dto.getMbti());
        user.setSelfIntroduction(dto.getSelfIntroduction());
        userAccountRepository.save(user);

        //관심사 리스트 삭제후 다시 저장하기.
        interestRepository.deleteAllByUserAccount(user);
        List<Interest> list = new ArrayList<>();
        for(String s: dto.getInterests()){
            list.add(Interest.of(user,s));
        }

        interestRepository.saveAll(list);

        return user;
    }

    public UserAccount getUser(Long otherId) {
        return userAccountRepository.findById(otherId).get();
    }
}
