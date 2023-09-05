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

@DisplayName("CustomUserDetailsService - 테스트")
@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @Mock private UserAccountRepository userAccountRepository;
    @InjectMocks private CustomUserDetailsService customUserDetailsService;

    @DisplayName("유저명으로 유저 조회하기 - 테스트")
    @Test
    void givenUsername_whenSelectUser_thenReturnUserAccount(){
        //Given
        UserAccount user = UserAccount.of("qweeqw","asdfdf", "nickname","asdf","asdf","M","하이요");
        given(userAccountRepository.findByUserId(user.getUsername())).willReturn(user);

        //When
        UserDetails result = customUserDetailsService.loadUserByUsername(user.getUsername());

        assertThat(result).isNotNull();
        assertThat(result).hasFieldOrPropertyWithValue("username","qweeqw");

    }

}