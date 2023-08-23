package com.blind.dating.service;

import com.blind.dating.domain.UserAccount;
import com.blind.dating.dto.user.UserAccountDto;
import com.blind.dating.dto.user.UserRequestDto;
import com.blind.dating.repository.UserAccountRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@Disabled
@DisplayName("회원 인증 비즈니스 로직 - 테스트")
@ExtendWith(MockitoExtension.class)
class UserAccountServiceTest {

    @InjectMocks private UserAccountService userAccountService;

    @Mock private UserAccountRepository userAccountRepository;

    @Mock private BCryptPasswordEncoder bCryptPasswordEncoder;

    @DisplayName("회원 가입 테스트")
    @Test
    void givenUserAccountDto_whenCreateUser_thenReturnUserAccount(){
        //Given
        UserRequestDto dto = UserRequestDto.of("user01","pqss01","user1","서울","INFP","M","gd");
        UserAccount user = dto.toEntity();
        user.setDeleted(false);

        given(userAccountRepository.save(any(UserAccount.class))).willReturn(user);


        //When
        UserAccount user1 = userAccountService.create(dto,"adsfasdfasdfsaf");

        //Then
        assertThat(user).isNotNull();

    }

    @DisplayName("로그인 회원정보 유무 테스트")
    @Test
    void givenUserIdANdUserPassword_whenCheckUser_thenReturnUser(){
        //Given
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

        // 모킹할 Repository

        // 테스트 데이터
        String userId = "user01";
        String userPassword = "pass01";

        // 모킹 설정
        UserAccountDto dto = UserAccountDto.of("user01","pass01","user1","서울","INFP","M",false,"gd");
        dto.setUserPassword(bCryptPasswordEncoder.encode(dto.getUserPassword()));
        given(userAccountRepository.findByUserId(userId)).willReturn(dto.toEntity());

        // 테스트 메소드 실행
        UserAccount actual = userAccountService.getByCredentials(userId, userPassword);

        // 모킹 검증
        then(userAccountRepository).should().findByUserId(userId);

        // 테스트 검증
        Assertions.assertThat(actual).isNotNull();

    }

    @DisplayName("유저아이디 중복 체크 - 아이디 존재하지 않는 경우")
    @Test
    void givenNonExistingUserId_whenCheckUserId_thenReturnBoolean(){
        // Given
        String nonExistingUserId = "nonExistingUser";
        given(userAccountRepository.findByUserId(nonExistingUserId)).willReturn(null);

        // When
        boolean result = userAccountService.checkUserId(nonExistingUserId);

        // Then
        then(userAccountRepository).should().findByUserId(nonExistingUserId);
        assertThat(result).isFalse();
    }

    @DisplayName("유저아이디 중복 체크 - 이미 존재하는 아이디인 경우")
    @Test
    void givenExistingUserId_whenCheckUserId_thenReturnTrue(){
        // Given
        String existingUserId = "existingUser";
        UserAccount existingUser = UserAccount.of("user01","pass01","user1","서울","INFP","M","gd");
        existingUser.setUserId(existingUserId);
        given(userAccountRepository.findByUserId(existingUserId)).willReturn(existingUser);

        // When
        boolean result = userAccountService.checkUserId(existingUserId);

        // Then
        then(userAccountRepository).should().findByUserId(existingUserId);
        assertThat(result).isTrue();
    }

    @DisplayName("닉네임 중복 체크 - 이미 존재하는 닉네임일 경우")
    @Test
    void givenExistingNickname_whenCheckNickname_thenReturnBoolean(){
        //Given
        String nickname = "user1";
        UserAccount existingUser = UserAccount.of("user01","pass01","user1","서울","INFP","M","gd");
        given(userAccountRepository.findByNickname(nickname)).willReturn(existingUser);

        //When
        boolean result = userAccountService.checkNickname(nickname);

        //Then
        then(userAccountRepository).should().findByNickname(nickname);
        assertThat(result).isTrue();
    }

    @DisplayName("닉네임 중복 체크 - 닉네임이 없는 경우")
    @Test
    void givenNonExistingNickname_whenCheckNickname_thenReturnBoolean(){
        //Given
        String nickname = "nick1";
        given(userAccountRepository.findByNickname(nickname)).willReturn(null);

        //When
        boolean result = userAccountService.checkNickname(nickname);

        //then
        then(userAccountRepository).should().findByNickname(nickname);
        assertThat(result).isFalse();
    }





}