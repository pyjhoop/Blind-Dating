package com.blind.dating.service;

import com.blind.dating.config.SecurityConfig;
import com.blind.dating.domain.Interest;
import com.blind.dating.domain.UserAccount;
import com.blind.dating.repository.InterestRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@DisplayName("관심사 서비스 테스트")
@ExtendWith(MockitoExtension.class)
class InterestServiceTest {

    @Mock
    private InterestRepository interestRepository;

    @InjectMocks
    private InterestService interestService;

    @DisplayName("관심사 저장")
    @Test
    void givenInterestList_whenSaveList_thenReturnList(){

        //Given
        UserAccount user = UserAccount.of("userId","pwd","nick","서울",12,"INFP","M");
        user.setDeleted(false);

        Authentication authentication = new UsernamePasswordAuthenticationToken(user,"pwd");

        Interest interest = Interest.of(user,"내용");
        List<Interest> list = List.of(interest);
        List<String> content = List.of("내용");
        given(interestRepository.saveAll(anyList())).willReturn(list);

        //When
        List<Interest> result = interestService.saveInterest(authentication, content);

        //Then
        assertThat(result).isNotEmpty();
        then(interestRepository).should().saveAll(anyList());
    }

    @DisplayName("관심사 업데이트")
    @Test
    void givenInterestList_thenUpdateInterest_thenReturnUpdatedList(){
        //Given
        UserAccount user = UserAccount.of("userId","pwd","nick","서울",12,"INFP","M");
        user.setDeleted(false);
        Authentication authentication = new UsernamePasswordAuthenticationToken(user, "pwd");
        Interest interest = Interest.of(user,"내용");
        List<Interest> list = List.of(interest);
        List<String> content = List.of("내용");
        given(interestRepository.saveAll(anyList())).willReturn(list);

        //When
        List<Interest> result = interestService.updateInterest(authentication, content);

        //Then
        assertThat(result).isNotEmpty();
        then(interestRepository).should().saveAll(anyList());

    }


}