package com.blind.dating.service;

import com.blind.dating.domain.user.CustomUserDetailsService;
import com.blind.dating.domain.user.UserAccount;
import com.blind.dating.domain.user.UserAccountRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;

@DisplayName("CustomUserDetailsService - 테스트")
@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @Mock private UserAccountRepository userAccountRepository;
    @InjectMocks private CustomUserDetailsService customUserDetailsService;

    @DisplayName("유저명으로 유저 조회하기 - 테스트")
    @Test
    void givenUsername_whenSelectUser_thenReturnUserAccount(){
        //Given
        UserAccount user = UserAccount.of("user01","pass01", "nickname","서울","infp","M","하이요");
        given(userAccountRepository.findByUserId(user.getUserId())).willReturn(Optional.of(user));

        //When
        UserDetails result = customUserDetailsService.loadUserByUsername(user.getUserId());

        assertThat(result).isNotNull();
        assertThat(result).hasFieldOrPropertyWithValue("username","user01");
    }
    @DisplayName("유저명으로 유저 조회시 예외발생 - 테스트")
    @Test
    void givenUsername_whenSelectUser_thenThrowException(){
        //Given
        UserAccount user = UserAccount.of("user01","pass01", "nickname","서울","infp","M","하이요");
        given(userAccountRepository.findByUserId(user.getUserId())).willReturn(Optional.empty());

        //When
        RuntimeException exception = assertThrows(RuntimeException.class, () ->{
            customUserDetailsService.loadUserByUsername(user.getUserId());
        });

        //Then
        assertThat(exception.getMessage()).isEqualTo("존재하지 않는 유저입니다.");
    }


}