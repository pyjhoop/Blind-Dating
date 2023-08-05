package com.blind.dating.service;

import com.blind.dating.domain.UserAccount;
import com.blind.dating.repository.UserAccountRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@DisplayName("CustomUseDetailsService 테스트")
@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @Mock
    private UserAccountRepository userAccountRepository;

    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;

    @DisplayName("유저 아이디로 계정 확인 테스트")
    @Test
    void givenUserId_whenSelectUser_thenReturnUser(){

        //Given
        UserAccount user = UserAccount.of("userId","pwd","nick","서울",12,"INFP","M");
        user.setDeleted(false);
        given(userAccountRepository.findByUserId("user01")).willReturn(user);

        //When
        UserDetails result = customUserDetailsService.loadUserByUsername("user01");

        //Then
        assertThat(result).isNotNull();
        then(userAccountRepository).should().findByUserId("user01");

    }

}