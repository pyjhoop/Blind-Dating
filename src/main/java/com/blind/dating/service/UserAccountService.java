package com.blind.dating.service;

import com.blind.dating.domain.UserAccount;
import com.blind.dating.dto.user.*;
import com.blind.dating.repository.RefreshTokenRepository;
import com.blind.dating.repository.UserAccountRedisRepository;
import com.blind.dating.repository.UserAccountRepository;
import com.blind.dating.security.TokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserAccountService {

    private final UserAccountRepository userAccountRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final TokenProvider tokenProvider;
    private final QuestionService questionService;
    private final InterestService interestService;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserAccountRedisRepository userAccountRedisRepository;

    /**
     * 회원가입 서비스 로직
     * @param dto
     * @return UserInfoWithTokens
     */
    @Transactional
    public UserAccount register(UserRequestDto dto){

        if(dto == null || dto.getUserId() == null){
            throw new RuntimeException("Invalid arguments");
        }

        // 아이디 존재하는지 체크
        String userId = dto.getUserId();
        if(userAccountRepository.existsByUserId(userId)){
            throw new RuntimeException("UserId already exists");
        }

        // 유저 저장하기
        UserAccount user = dto.toEntity();
        user.setRecentLogin(LocalDateTime.now());
        user.setDeleted(false);
        user.setUserPassword(bCryptPasswordEncoder.encode(dto.getUserPassword()));

        UserAccount resultUser =  userAccountRepository.save(user);
        //questions 저장하기
        questionService.saveQuestions(resultUser,dto.getQuestions());
        // interests 저장하기
        interestService.saveInterest(resultUser, dto.getInterests());
        return resultUser;
    }

    /**
     * 로그인 서비스
     * @param userId
     * @param userPassword
     * @return UserInfoWithTokens
     */
    @Transactional
    public LogInResponse getLoginInfo(String userId, String userPassword){

        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

        //userId로 유저 정보 가져오기
        UserAccount user = userAccountRepository.findByUserId(userId);
        //userAccountId로 refreshToken 가져오기

        user.setRecentLogin(LocalDateTime.now());

        // 비밀번호 맞는지 확인하기.
        if(bCryptPasswordEncoder.matches(userPassword,user.getUserPassword())){
            //토큰 생성하기
            String accessToken = tokenProvider.create(user);
            String refreshToken = tokenProvider.refreshToken(user);

            //UserInfo 캐싱하기
            userAccountRedisRepository.saveUser(String.valueOf(user.getId()), new UserIdWithNicknameAndGender(user.getId(),user.getNickname(), user.getGender()));

            //refresh Token 캐싱하기
            refreshTokenRepository.save(String.valueOf(user.getId()), refreshToken);

            LogInResponse response = LogInResponse.from(user);
            response.setAccessToken(accessToken);
            response.setRefreshToken(refreshToken);

            return response;

        }else{
            return null;
        }
    }

    /**
     * 유저 아이디 확인 서비스
     * @param userId
     * @return boolean
     */
    public boolean checkUserId(String userId){
        UserAccount user = userAccountRepository.findByUserId(userId);

        return user != null;

    }

    /**
     * 유저 닉네임 확인 서비스
     * @param nickname
     * @return boolean
     */
    public boolean checkNickname(String nickname){
        UserAccount user = userAccountRepository.findByNickname(nickname);
        return user != null;
    }

    /**
     * 회원가입시 validation 에러가 발생할 때 확인하는 메서드.
     * @param errors
     * @return Map<String, String>
     */
    public Map<String, String> validateHandling(Errors errors) {
        Map<String, String> validatorResult = new HashMap<>();

        for (FieldError error : errors.getFieldErrors()) {
            String validKeyName = String.format("valid_%s", error.getField());
            validatorResult.put(validKeyName, error.getDefaultMessage());
        }

        return validatorResult;
    }
}
