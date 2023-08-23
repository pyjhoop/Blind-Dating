package com.blind.dating.service;

import com.blind.dating.domain.UserAccount;
import com.blind.dating.repository.UserAccountRepository;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.awt.print.Book;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@Disabled
@DisplayName("유저 조회 서비스")
@ExtendWith(MockitoExtension.class)
class UserServiceTest {


    @InjectMocks
    private UserService userService;

    @Mock
    private UserAccountRepository userAccountRepository;


    @Disabled
    @DisplayName("추천 유저리스트 조회")
    @Test
    void givenData_whenSearchUserList_thenReturnRecommendedUserList(){

        //Given
        Pageable pageable = Pageable.ofSize(10);
        //given(userAccountRepository.findAllByScoreBetweenAndGender(5,15,"M",pageable)).willReturn(Page.empty());

        //When
        //Page<UserAccount> pages = userService.getUserList(10,"M",pageable,"1");

        //assertThat(pages).isEmpty();
        //then(userAccountRepository).should().findAllByScoreBetweenAndGender(5,15,"M",pageable);
    }

    @DisplayName("내 정보 조회하기")
    @Test
    void giveUserId_whenSearchMyInfo_thenReturnMyInfo(){
        //Given
        UserAccount user = UserAccount.of("userId","pwd","nick","서울","INFP","M","하요");
        user.setDeleted(false);
        given(userAccountRepository.findByUserId(user.getUserId())).willReturn(user);

        //When
        UserAccount result = userService.getMyInfo(user.getUserId());

        //Then
        assertThat(result).hasFieldOrPropertyWithValue("userId",user.getUserId())
                .isNotNull();
        then(userAccountRepository).should().findByUserId(user.getUserId());
    }


}