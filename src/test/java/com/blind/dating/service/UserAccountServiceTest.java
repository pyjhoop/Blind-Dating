package com.blind.dating.service;

import com.blind.dating.domain.UserAccount;
import com.blind.dating.dto.UserAccountDto;
import com.blind.dating.repository.UserAccountRepository;
import org.assertj.core.api.Assertions;
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

@DisplayName("회원 인증 비즈니스 로직 - 테스트")
@ExtendWith(MockitoExtension.class)
class UserAccountServiceTest {

    @InjectMocks private UserAccountService userAccountService;

    @Mock private UserAccountRepository userAccountRepository;

    @DisplayName("회원 가입 테스트")
    @Test
    void givenUserAccountDto_whenCreateUser_thenReturnUserAccount(){
        //Given
        UserAccountDto dto = UserAccountDto.of("user01","pqss01","user1","서울",12,"INFP","M",false);

        given(userAccountRepository.save(dto.toEntity())).willReturn(any(UserAccount.class));

        //When
        UserAccount user = userAccountService.create(dto);

        //Then
        then(userAccountRepository).should().findByUserId("user01");

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
        UserAccountDto dto = UserAccountDto.of("user01","pass01","user1","서울",12,"INFP","M",false);
        dto.setUserPassword(bCryptPasswordEncoder.encode(dto.getUserPassword()));
        given(userAccountRepository.findByUserId(userId)).willReturn(dto.toEntity());

        // 테스트 메소드 실행
        UserAccount actual = userAccountService.getByCredentials(userId, userPassword);

        // 모킹 검증
        then(userAccountRepository).should().findByUserId(userId);

        // 테스트 검증
        Assertions.assertThat(actual).isNotNull();

    }

}