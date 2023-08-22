package com.blind.dating.service;

import com.blind.dating.domain.UserAccount;
import com.blind.dating.dto.user.UserRequestDto;
import com.blind.dating.repository.UserAccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserAccountService {

    private final UserAccountRepository userAccountRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public Map<String, String> validateHandling(Errors errors) {
        Map<String, String> validatorResult = new HashMap<>();

        for (FieldError error : errors.getFieldErrors()) {
            String validKeyName = String.format("valid_%s", error.getField());
            validatorResult.put(validKeyName, error.getDefaultMessage());
        }

        return validatorResult;
    }

    @Transactional
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
        user.setRecentLogin(LocalDateTime.now());
        user.setRefreshToken(refreshToken);
        user.setDeleted(false);
        user.setUserPassword(bCryptPasswordEncoder.encode(dto.getUserPassword()));

        return userAccountRepository.save(user);
    }

    @Transactional
    public UserAccount getByCredentials(String userId, String userPassword){
        log.info(userId + userPassword);

        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

        UserAccount user = userAccountRepository.findByUserId(userId);
        user.setRecentLogin(LocalDateTime.now());

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
