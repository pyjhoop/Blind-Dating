package com.blind.dating.service;

import com.blind.dating.common.code.ResponseCode;
import com.blind.dating.common.code.UserResponseCode;
import com.blind.dating.domain.interest.Interest;
import com.blind.dating.domain.interest.InterestRepository;
import com.blind.dating.domain.user.UserAccount;
import com.blind.dating.domain.user.UserAccountService;
import com.blind.dating.domain.user.dto.LogInResponse;
import com.blind.dating.domain.user.dto.UserRequestDto;
import com.blind.dating.exception.ApiException;
import com.blind.dating.domain.token.RefreshTokenRepository;
import com.blind.dating.domain.user.UserAccountRedisRepository;
import com.blind.dating.domain.user.UserAccountRepository;
import com.blind.dating.security.TokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;

import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@DisplayName("유저 조회 서비스")
@ExtendWith(MockitoExtension.class)
class UserAccountServiceTest {

    @InjectMocks private UserAccountService userAccountService;
    @Mock private UserAccountRepository userAccountRepository;
    @Spy
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Mock private TokenProvider tokenProvider;
    @Mock private UserAccountRedisRepository userAccountRedisRepository;
    @Mock private RefreshTokenRepository refreshTokenRepository;
    @Mock private InterestRepository interestRepository;
    @Mock private Errors errors;

    private UserRequestDto dto;
    private UserAccount user;

    @BeforeEach
    void setting(){
        dto = UserRequestDto.of("userId@gmail.com","userPass01","userNickname","서울","INFP","M","안녕하세요");
        dto.setInterests(List.of(1L, 2L));
        user = dto.toEntity();
        user.setRecentLogin(LocalDateTime.now());
        user.setDeleted(false);
        user.setUserPassword(bCryptPasswordEncoder.encode(dto.getUserPassword()));
        user.setInterests(List.of(new Interest()));
    }



    @DisplayName("회원가입 - 테스트")
    @Test
    void givenUserRequestDto_whenRegister_thenRegisterSuccess(){
        //given
        String accessToken = "asdffqwerqwerdfgscvASDF";
        String password = "hashPass";
        List<Interest> list1 = List.of();

        given(userAccountRepository.existsByEmail(dto.getEmail())).willReturn(false);// 존재 x
        given(bCryptPasswordEncoder.encode(dto.getUserPassword())).willReturn(password);
        given(userAccountRepository.save(user)).willReturn(user);
        given(interestRepository.findAllByIdIn(dto.getInterests())).willReturn(list1);

        //when
        userAccountService.register(dto);

        //then
        verify(userAccountRepository, times(1)).existsByEmail(dto.getEmail());

    }

    @DisplayName("회원가입시 예외 발생 - 테스트")
    @Test
    void givenUserRequestDto_whenRegister_thenThrowException(){
        //given
        given(userAccountRepository.existsByEmail(dto.getEmail())).willReturn(true);// 존재 x

        //when
        ApiException exception = assertThrows(ApiException.class, ()-> {
            userAccountService.register(dto);
        });

        //then
        assertThat(exception.getResponseCode()).isEqualTo(UserResponseCode.EXIST_EMAIL);

    }



    @DisplayName("로그인 서비스 - 성공 테스트")
    @Test
    void givenLoginInfo_whenLogin_thenReturnLogInResponseSuccess(){
        //given
        String userId = "userId";
        String password = "userPass01";
        LogInResponse response = LogInResponse.from(user,"accessToken","refreshToken");

        given(userAccountRepository.findByEmail(userId)).willReturn(Optional.of(user));
        // When
        LogInResponse user1 = userAccountService.getLoginInfo(userId, password);

        // Then
        assertThat(user1.getEmail()).isEqualTo(dto.getEmail());
    }

    @DisplayName("로그인 서비스실패 후 예외 - 테스트")
    @Test
    void givenLoginInfo_whenLogin_thenReturnLogInThrowException(){
        //given
        String password = "userPass01";
        LogInResponse response = LogInResponse.from(user,"accessToken","refreshToken");

        given(userAccountRepository.findByEmail(dto.getEmail())).willReturn(Optional.empty());
        // When
        ApiException exception = assertThrows(ApiException.class, ()->{
            LogInResponse user1 = userAccountService.getLoginInfo(dto.getEmail(), password);
        });

        // Then
        assertThat(exception.getResponseCode()).isEqualTo(UserResponseCode.LOGIN_FAIL);
        verify(tokenProvider, never()).create(user);
    }

    @DisplayName("로그인시 비밀번호 매치 실패")
    @Test
    void givenLoginInfo_whenLogin_thenReturnLogInPasswordMismatch(){
        //given
        String password = "userPass01";
        LogInResponse response = LogInResponse.from(user,"accessToken","refreshToken");

        given(userAccountRepository.findByEmail(dto.getEmail())).willReturn(Optional.of(user));
        given(bCryptPasswordEncoder.matches(password, user.getUserPassword())).willReturn(false);
        // When
        ApiException exception = assertThrows(ApiException.class, ()->{
            LogInResponse user1 = userAccountService.getLoginInfo(dto.getEmail(), password);
        });

        // Then
        assertThat(exception.getResponseCode()).isEqualTo(UserResponseCode.NOT_MATCH_PASSWORD);
    }

    @DisplayName("유저정보 조회 실패로 로그인 실패 - 테스트")
    @Test
    void givenLoginInfo_whenLoginFail_thenReturnLogInThrowException() {
        // Given
        given(userAccountRepository.findByEmail(anyString())).willReturn(Optional.empty());

        // When
        ApiException exception = assertThrows(ApiException.class, ()-> {
            userAccountService.getLoginInfo("user01", "pass01");
        });

        // Then
        assertThat(exception.getResponseCode()).isEqualTo(UserResponseCode.LOGIN_FAIL);


    }





    @DisplayName("유저아이디 확인 테스트 - 아이디 존재")
    @Test
    void givenUserId_whenCheckUserId_thenReturnTrue(){
        //given
        given(userAccountRepository.existsByEmail(dto.getEmail())).willReturn(false);

        //when
        ResponseCode result = userAccountService.checkUserEmail(dto.getEmail());

        //then
        assertThat(result).isEqualTo(UserResponseCode.NOT_EXIST_EMAIL);

    }

    @DisplayName("유저아이디 확인 테스트 - 아이디 없음.")
    @Test
    void givenUserId_whenCheckUserId_thenReturnFalse(){
        //given
        given(userAccountRepository.existsByEmail(dto.getEmail())).willReturn(true);


        ResponseCode result = userAccountService.checkUserEmail(dto.getEmail());


        //then
        assertThat(result).isEqualTo(UserResponseCode.EXIST_EMAIL);

    }

    @DisplayName("유저 닉네임 확인 테스트 - 닉네임 있음.")
    @Test
    void givenUserNickname_whenCheckUserNickname_thenReturnFalse(){
        //given
        String userNickname = "nick11";
        UserAccount user = UserAccount.of("qweeqw","asdfdf", userNickname,"asdf","asdf","M","하이요");
        given(userAccountRepository.existsByNickname(userNickname)).willReturn(true);

        //when
        ResponseCode result = userAccountService.checkNickname(userNickname);

        //then
        assertThat(result).isEqualTo(UserResponseCode.EXIST_NICKNAME);

    }
    @DisplayName("유저 닉네임 확인 테스트 - 닉네임 있음.")
    @Test
    void givenUserNickname_whenCheckUserNickname_thenReturnTrue(){
        //given
        String userNickname = "nick11";
        UserAccount user = UserAccount.of("qweeqw","asdfdf", userNickname,"asdf","asdf","M","하이요");
        given(userAccountRepository.existsByNickname(userNickname)).willReturn(false);

        //when
        ResponseCode result = userAccountService.checkNickname(userNickname);

        //then
        assertThat(result).isEqualTo(UserResponseCode.NOT_EXIST_NICKNAME);

    }

    @DisplayName("회원가입 validation - 테스트")
    @Test
    void givenErrors_whenCheckErrors_thenReturnValidatorResult() {
        FieldError fieldError1 = new FieldError("objectName", "fieldName1", "errorMessage1");
        FieldError fieldError2 = new FieldError("objectName", "fieldName2", "errorMessage2");
        given(errors.getFieldErrors()).willReturn(Arrays.asList(fieldError1, fieldError2));

        // validateHandling 메소드 실행
        Map<String, String> result = userAccountService.validateHandling(errors);

        // 결과 확인
        Map<String, String> expected = new HashMap<>();
        expected.put("valid_fieldName1", "errorMessage1");
        expected.put("valid_fieldName2", "errorMessage2");
        assertThat(result).isEqualTo(expected);
    }

}