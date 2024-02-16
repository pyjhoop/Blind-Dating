package com.blind.dating.service;

import com.blind.dating.common.code.ResponseCode;
import com.blind.dating.common.code.UserResponseCode;
import com.blind.dating.domain.interest.Interest;
import com.blind.dating.domain.interest.InterestService;
import com.blind.dating.domain.user.UserAccount;
import com.blind.dating.domain.user.UserAccountService;
import com.blind.dating.dto.user.LogInResponse;
import com.blind.dating.dto.user.UserRequestDto;
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
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

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
    @Mock private QuestionService questionService;
    @Mock private InterestService interestService;
    @Mock private Errors errors;

    private UserRequestDto dto;
    private UserAccount user;

    @BeforeEach
    void setting(){
        dto = UserRequestDto.of("userId","userPass01","userNickname","서울","INFP","M","안녕하세요");
        dto.setInterests(List.of("자전거타기","놀기","게임하기"));
        dto.setQuestions(List.of(true, false, true));
        user = dto.toEntity();
        user.setRecentLogin(LocalDateTime.now());
        user.setDeleted(false);
        user.setUserPassword(bCryptPasswordEncoder.encode(dto.getUserPassword()));
        user.setInterests(List.of(new Interest()));
        user.setQuestions(List.of(new Question()));
    }



    @DisplayName("회원가입 - 테스트")
    @Test
    void givenUserRequestDto_whenRegister_thenRegisterSuccess(){
        //given
        String accessToken = "asdffqwerqwerdfgscvASDF";
        String password = "hashPass";
        List<Question> list = List.of();
        List<Interest> list1 = List.of();

        given(userAccountRepository.existsByUserId(dto.getUserId())).willReturn(false);// 존재 x
        given(bCryptPasswordEncoder.encode(dto.getUserPassword())).willReturn(password);
        given(userAccountRepository.save(user)).willReturn(user);
        given(questionService.saveQuestions(user, dto.getQuestions())).willReturn(list);
        given(interestService.saveInterest(user, dto.getInterests())).willReturn(list1);

        //when
        UserAccount info = userAccountService.register(dto);

        //then
        assertThat(info).isNotNull()
                .hasFieldOrPropertyWithValue("id",info.getId());

    }

    @DisplayName("회원가입시 예외 발생 - 테스트")
    @Test
    void givenUserRequestDto_whenRegister_thenThrowException(){
        //given
        String accessToken = "asdffqwerqwerdfgscvASDF";
        String password = "hashPass";
        List<Question> list = List.of();
        List<Interest> list1 = List.of();

        given(userAccountRepository.existsByUserId(dto.getUserId())).willReturn(true);// 존재 x

        //when
        ApiException exception = assertThrows(ApiException.class, ()-> {
            UserAccount info = userAccountService.register(dto);
        });

        //then
        assertThat(exception.getResponseCode()).isEqualTo(UserResponseCode.EXIST_USER_ID);

    }



    @DisplayName("로그인 서비스 - 성공 테스트")
    @Test
    void givenLoginInfo_whenLogin_thenReturnLogInResponseSuccess(){
        //given
        String userId = "userId";
        String password = "userPass01";
        LogInResponse response = LogInResponse.from(user,"accessToken","refreshToken");

        given(userAccountRepository.findByUserId(userId)).willReturn(Optional.of(user));
        // When
        LogInResponse user1 = userAccountService.getLoginInfo(userId, password);

        // Then
        assertThat(user1.getUserId()).isEqualTo(userId);
    }

    @DisplayName("로그인 서비스실패 후 예외 - 테스트")
    @Test
    void givenLoginInfo_whenLogin_thenReturnLogInThrowException(){
        //given
        String userId = "userId";
        String password = "userPass01";
        LogInResponse response = LogInResponse.from(user,"accessToken","refreshToken");

        given(userAccountRepository.findByUserId(userId)).willReturn(Optional.of(user));
        given(bCryptPasswordEncoder.matches(password, user.getUserPassword())).willReturn(false);
        // When
        RuntimeException exception = assertThrows(RuntimeException.class, ()->{
            LogInResponse user1 = userAccountService.getLoginInfo(userId, password);
        });

        // Then
        assertThat(exception.getMessage()).isEqualTo("Not Found UserInfo");
        verify(tokenProvider, never()).create(user);
    }

    @DisplayName("유저정보 조회 실패로 로그인 실패 - 테스트")
    @Test
    void givenLoginInfo_whenLoginFail_thenReturnLogInThrowException() {
        // Given
        given(userAccountRepository.findByUserId(anyString())).willReturn(Optional.empty());

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
        String userId = "userId";
        UserAccount user = UserAccount.of(userId,"asdfdf", "nick1","asdf","asdf","M","하이요");
        given(userAccountRepository.existsByUserId(userId)).willReturn(false);

        //when
        ResponseCode result = userAccountService.checkUserId(userId);

        //then
        assertThat(result).isEqualTo(UserResponseCode.NOT_EXIST_USER_ID);

    }

    @DisplayName("유저아이디 확인 테스트 - 아이디 없음.")
    @Test
    void givenUserId_whenCheckUserId_thenReturnFalse(){
        //given
        String userId = "userId";
        UserAccount user = UserAccount.of(userId,"asdfdf", "nick1","asdf","asdf","M","하이요");
        given(userAccountRepository.existsByUserId(userId)).willReturn(true);


        ResponseCode result = userAccountService.checkUserId(userId);


        //then
        assertThat(result).isEqualTo(UserResponseCode.EXIST_USER_ID);

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