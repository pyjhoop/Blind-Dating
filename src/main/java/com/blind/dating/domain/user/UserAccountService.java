package com.blind.dating.domain.user;

import com.blind.dating.common.code.ResponseCode;
import com.blind.dating.domain.interest.Interest;
import com.blind.dating.domain.interest.InterestRepository;
import com.blind.dating.domain.user.dto.LogInResponse;
import com.blind.dating.domain.user.dto.UserRequestDto;
import com.blind.dating.exception.ApiException;
import com.blind.dating.domain.token.RefreshTokenRepository;
import com.blind.dating.security.TokenProvider;
import com.blind.dating.common.code.UserResponseCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserAccountService {

    private final UserAccountRepository userAccountRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final TokenProvider tokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final InterestRepository interestRepository;

    /**
     * 회원가입 서비스 로직
     * @param dto
     * @return UserInfoWithTokens
     */
    @Transactional
    public void register(UserRequestDto dto){

        // 아이디 존재하는지 체크
        String email = dto.getEmail();
        if(userAccountRepository.existsByEmail(email)){
            throw new ApiException(UserResponseCode.EXIST_EMAIL);
        }
        // 유저 저장하기
        UserAccount user = dto.toRegisterEntity(bCryptPasswordEncoder.encode(dto.getUserPassword()));
        userAccountRepository.save(user);
        List<Interest> interests = interestRepository.findAllByIdIn(dto.getInterests());
        user.setInterests(interests);
    }

    /**
     * 로그인 서비스
     * @param email
     * @param userPassword
     * @return UserInfoWithTokens
     */
    @Transactional
    public LogInResponse getLoginInfo(String email, String userPassword){

        //userId로 유저 정보 가져오기
        UserAccount user = userAccountRepository.findByEmail(email)
                .orElseThrow(()-> new ApiException(UserResponseCode.LOGIN_FAIL));
        user.setRecentLogin(LocalDateTime.now());

        // 비밀번호 맞는지 확인하기.
        if(bCryptPasswordEncoder.matches(userPassword,user.getUserPassword())){
            //토큰 생성하기
            String accessToken = tokenProvider.create(user);
            String refreshToken = tokenProvider.refreshToken(user);

            //refresh Token 캐싱하기
            refreshTokenRepository.save(user.getId(), refreshToken);

            return LogInResponse.from(user, accessToken, refreshToken);
        }else{
            throw new ApiException(UserResponseCode.NOT_MATCH_PASSWORD);
        }
    }

    /**
     * 이메일 중복 확인
     * @param email
     * @return boolean
     */
    public UserResponseCode checkUserEmail(String email){
        Boolean status = userAccountRepository.existsByEmail(email);

        return (status) ? UserResponseCode.EXIST_EMAIL : UserResponseCode.NOT_EXIST_EMAIL;
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
