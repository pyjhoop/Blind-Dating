package com.blind.dating.service;

import com.blind.dating.domain.UserAccount;
import com.blind.dating.dto.user.UserRequestDto;
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
    private final BCryptPasswordEncoder bCryptPasswordEncoder;


    public UserAccount create(UserRequestDto dto, String refreshToken){

        if(dto == null || dto.getUserId() == null){
            throw new RuntimeException("Invalid arguments");
        }

        // 아이디 존재하는지 체크
        String userId = dto.getUserId();
        if(userAccountRepository.existsByUserId(userId)){
            log.warn("UserId already exists {}", userId);
            throw new RuntimeException("UserId already exists");
        }

        UserAccount user = dto.toEntity();
        user.setRefreshToken(refreshToken);
        user.setDeleted(false);
        user.setUserPassword(bCryptPasswordEncoder.encode(dto.getUserPassword()));

        return userAccountRepository.save(user);
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

    public boolean checkUserId(String userId){
        UserAccount user = userAccountRepository.findByUserId(userId);

        return user != null;

    }

    public boolean checkNickname(String nickname){
        UserAccount user = userAccountRepository.findByNickname(nickname);
        return user != null;
    }
}
