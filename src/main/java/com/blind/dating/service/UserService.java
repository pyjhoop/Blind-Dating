package com.blind.dating.service;

import com.blind.dating.domain.UserAccount;
import com.blind.dating.repository.UserAccountRepository;
import com.blind.dating.repository.querydsl.UserAccountRepositoryImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
@Slf4j
public class UserService {

    private final UserAccountRepository userAccountRepository;
    private final UserAccountRepositoryImpl userAccountRepositoryImpl;

    public Page<UserAccount> getUserList(String gender, Authentication authentication, Pageable pageable){

        UserAccount user = (UserAccount) authentication.getPrincipal();
        log.warn(String.valueOf(user.getId()));


        return userAccountRepositoryImpl.findAllByGenderAndNotLikes(gender, user.getId(), pageable);

    }

    public UserAccount getMyInfo(String userId){
        return userAccountRepository.findByUserId(userId);
    }
}
