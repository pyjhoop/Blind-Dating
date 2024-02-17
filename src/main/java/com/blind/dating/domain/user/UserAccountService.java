package com.blind.dating.domain.user;

import com.blind.dating.common.code.ResponseCode;
import com.blind.dating.domain.user.dto.LogInResponse;
import com.blind.dating.domain.user.dto.UserRequestDto;
import com.blind.dating.exception.ApiException;
import com.blind.dating.domain.token.RefreshTokenRepository;
import com.blind.dating.security.TokenProvider;
import com.blind.dating.common.code.UserResponseCode;
import com.blind.dating.domain.interest.InterestService;
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
    private final TokenProvider tokenProvider;
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

        // 아이디 존재하는지 체크
        String userId = dto.getUserId();
        if(userAccountRepository.existsByUserId(userId)){
            throw new ApiException(UserResponseCode.EXIST_USER_ID);
        }
        // 유저 저장하기
        UserAccount user = dto.toRegisterEntity(bCryptPasswordEncoder.encode(dto.getUserPassword()));

        user.setInterests(interestService.saveInterest(user, dto.getInterests()));
        return userAccountRepository.save(user);
    }

    /**
     * 로그인 서비스
     * @param userId
     * @param userPassword
     * @return UserInfoWithTokens
     */
    @Transactional
    public LogInResponse getLoginInfo(String userId, String userPassword){

        //userId로 유저 정보 가져오기
        UserAccount user = userAccountRepository.findByUserId(userId)
                .orElseThrow(()-> new ApiException(UserResponseCode.LOGIN_FAIL));
        user.setRecentLogin(LocalDateTime.now());

        // 비밀번호 맞는지 확인하기.
        if(bCryptPasswordEncoder.matches(userPassword,user.getUserPassword())){
            //토큰 생성하기
            String accessToken = tokenProvider.create(user);
            String refreshToken = tokenProvider.refreshToken(user);

            //refresh Token 캐싱하기
            refreshTokenRepository.save(String.valueOf(user.getId()), refreshToken);

            return LogInResponse.from(user, accessToken, refreshToken);
        }else{
            throw new ApiException(UserResponseCode.NOT_MATCH_PASSWORD);
        }
    }

    /**
     * 유저 아이디 확인 서비스
     * @param userId
     * @return boolean
     */
    public UserResponseCode checkUserId(String userId){
        Boolean status = userAccountRepository.existsByUserId(userId);

        return (status) ? UserResponseCode.EXIST_USER_ID : UserResponseCode.NOT_EXIST_USER_ID;
    }

    /**
     * 유저 닉네임 확인 서비스
     * @param nickname
     * @return boolean
     */
    public ResponseCode checkNickname(String nickname){
        Boolean flag = userAccountRepository.existsByNickname(nickname);

        return (flag)? UserResponseCode.EXIST_NICKNAME: UserResponseCode.NOT_EXIST_NICKNAME;
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
