package com.blind.dating.service;

import com.blind.dating.domain.UserAccount;
import com.blind.dating.dto.UserAccountDto;
import com.blind.dating.repository.UserAccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserAccountService {

    private final UserAccountRepository userAccountRepository;


    public UserAccount create(UserAccountDto dto){
        if(dto == null || dto.getUserId() == null){
            throw new RuntimeException("Invalid arguments");
        }
        String userId = dto.getUserId();
        if(userAccountRepository.existsByUserId(userId)){
            log.warn("UserId already exists {}", userId);
            throw new RuntimeException("UserId already exists");
        }

        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

        dto.setDeleted(false);
        dto.setUserPassword(bCryptPasswordEncoder.encode(dto.getUserPassword()));
        userAccountRepository.save(dto.toEntity());
        return userAccountRepository.findByUserId(dto.getUserId());
    }

    public UserAccount getByCredentials(String userId, String userPassword){
        log.info(userId + userPassword);

        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

        UserAccount user = userAccountRepository.findByUserId(userId);

        if(bCryptPasswordEncoder.matches(userPassword,user.getUserPassword())){
            return user;
        }else{
            return null;
        }
    }
}
