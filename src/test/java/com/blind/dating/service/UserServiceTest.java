package com.blind.dating.service;

import com.blind.dating.domain.UserAccount;
import com.blind.dating.dto.user.UserUpdateRequestDto;
import com.blind.dating.repository.InterestRepository;
import com.blind.dating.repository.UserAccountRepository;
import com.blind.dating.repository.querydsl.UserAccountRepositoryImpl;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@Disabled

@DisplayName("UserService 테스트")
@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @InjectMocks private UserService userService;
    @Mock private UserAccountRepository userAccountRepository;
    @Mock private UserAccountRepositoryImpl userAccountRepositoryImpl;
    @Mock private InterestRepository interestRepository;


    @DisplayName("추천리스트 테스트")
    @Test
    void givenRequireData_whenSelectList_thenReturnUserList(){

        UserAccount user = UserAccount.of("qweeqw","asdfdf", "nickname","asdf","asdf","M","하이요");
        String gender ="M";

        Authentication authentication = new UsernamePasswordAuthenticationToken(user,user.getPassword());
        Pageable pageable = Pageable.ofSize(10);
        List<UserAccount> list = List.of(user);
        Page<UserAccount> pages = new PageImpl<>(list, pageable, 10);

        given(userAccountRepositoryImpl.findAllByGenderAndNotLikes("W",user.getId(), pageable)).willReturn(pages);

        Page<UserAccount> result = userService.getUserList(authentication, pageable);

        assertThat(result).isNotNull();
        assertThat(result.getTotalPages()).isEqualTo(1);

    }

    @DisplayName("내정보 조회 테스트")
    @Test
    void givenUser_whenGetMyInfo_thenReturnUserInfo(){
        //Given
        UserAccount user = UserAccount.of("qweeqw","asdfdf", "nickname","asdf","asdf","M","하이요");
        Authentication authentication = new UsernamePasswordAuthenticationToken(user,user.getPassword());
        given(userAccountRepository.findByUserId(user.getUserId())).willReturn(user);

        //When
        UserAccount result = userService.getMyInfo(authentication);

        //Then
        assertThat(result).isNotNull();
    }

    @DisplayName("내 정보 수정하기")
    @Test
    void givenUser_whenUpdateMyInfo_thenReturnUserInfo(){
        //Given
        UserAccount user = UserAccount.of("qweeqw","asdfdf", "nickname","asdf","asdf","M","하이요");
        Authentication authentication = new UsernamePasswordAuthenticationToken(user,user.getPassword());
        List<String> list = new ArrayList<>();
        list.add("놀기");
        list.add("피하기");
        UserUpdateRequestDto dto = UserUpdateRequestDto.builder()
                .region("인천")
                .mbti("MBTI")
                .selfIntroduction("하이요")
                .interests(list).build();

        given(userAccountRepository.save(any(UserAccount.class))).willReturn(user);

        UserAccount result = userService.updateMyInfo(authentication, dto);

        assertThat(result).isNotNull();
    }
}
