package com.blind.dating.service;

import com.blind.dating.domain.UserAccount;
import com.blind.dating.dto.user.UserInfoWithTokens;
import com.blind.dating.dto.user.UserRequestDto;
import com.blind.dating.repository.UserAccountRepository;
import com.blind.dating.security.TokenProvider;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@Disabled
@DisplayName("유저 조회 서비스")
@ExtendWith(MockitoExtension.class)
class UserAccountServiceTest {

    @InjectMocks private UserAccountService userAccountService;
    @Mock private UserAccountRepository userAccountRepository;
    @Spy
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Mock private TokenProvider tokenProvider;
    @Mock private QuestionService questionService;
    @Mock private InterestService interestService;

    @Disabled
    @DisplayName("회원가입 테스트")
    @Test
    void givenUserRequestDto_whenRegister_thenRegisterSuccess(){
        //given
        UserRequestDto dto = UserRequestDto.of("userId","userPass01","userNickname","서울","INFP","M","안녕하세요");
        dto.setInterests(List.of("자전거타기","놀기","게임하기"));
        dto.setQuestions(List.of(true, false, true));

        String accessToken = "asdffqwerqwerdfgscvASDF";
        String password = "hashPass";

        UserAccount user = dto.toEntity();
        user.setRecentLogin(LocalDateTime.now());
        user.setDeleted(false);

        user.setUserPassword(bCryptPasswordEncoder.encode(dto.getUserPassword()));

        given(userAccountRepository.existsByUserId(dto.getUserId())).willReturn(false);
        given(tokenProvider.create(dto.toEntity())).willReturn(accessToken);
        given(bCryptPasswordEncoder.encode(dto.getUserPassword())).willReturn(password);
        given(userAccountRepository.save(user)).willReturn(user);

        //when
        UserAccount info = userAccountService.register(dto);

        //then
        assertThat(info).isNotNull()
                .hasFieldOrPropertyWithValue("id",info.getId());

    }


    @Disabled
    @DisplayName("로그인 서비스 - 성공 테스트")
    @Test
    void givenLoginInfo_whenLogin_thenReturnUserInfoWithTokens(){
        //given
        String userId = "userId";
        String password = "asdffsdas123";
        String accessToken = "asdffasd";
        String encodedPassword = bCryptPasswordEncoder.encode(password);

        System.out.println(encodedPassword);
        UserAccount user = UserAccount.of(userId,encodedPassword, "nick1","asdf","asdf","M","하이요");


        given(userAccountRepository.findByUserId(userId)).willReturn(user);
        given(tokenProvider.create(user)).willReturn(accessToken);

        //when
        UserInfoWithTokens userInfo = userAccountService.getLoginInfo(userId, password);

        //then
        assertThat(userInfo).isNotNull()
                .hasFieldOrPropertyWithValue("accessToken", accessToken)
                .hasFieldOrPropertyWithValue("nickname",user.getNickname());
    }


    @DisplayName("유저아이디 확인 테스트 - 아이디 존재")
    @Test
    void givenUserId_whenCheckUserId_thenReturnTrue(){
        //given
        String userId = "userId";
        UserAccount user = UserAccount.of(userId,"asdfdf", "nick1","asdf","asdf","M","하이요");
        given(userAccountRepository.findByUserId(userId)).willReturn(user);

        //when
        boolean result = userAccountService.checkUserId(userId);

        //then
        assertThat(result).isTrue();

    }

    @DisplayName("유저아이디 확인 테스트 - 아이디 없음.")
    @Test
    void givenUserId_whenCheckUserId_thenReturnFalse(){
        //given
        String userId = "userId";
        UserAccount user = UserAccount.of(userId,"asdfdf", "nick1","asdf","asdf","M","하이요");
        given(userAccountRepository.findByUserId(userId)).willReturn(null);

        //when
        boolean result = userAccountService.checkUserId(userId);

        //then
        assertThat(result).isFalse();

    }

    @DisplayName("유저 닉네임 확인 테스트 - 닉네임 없음.")
    @Test
    void givenUserNickname_whenCheckUserNickname_thenReturnFalse(){
        //given
        String userNickname = "nick11";
        UserAccount user = UserAccount.of("qweeqw","asdfdf", userNickname,"asdf","asdf","M","하이요");
        given(userAccountRepository.findByNickname(userNickname)).willReturn(null);

        //when
        boolean result = userAccountService.checkNickname(userNickname);

        //then
        assertThat(result).isFalse();

    }
    @DisplayName("유저 닉네임 확인 테스트 - 닉네임 없음.")
    @Test
    void givenUserNickname_whenCheckUserNickname_thenReturnTrue(){
        //given
        String userNickname = "nick11";
        UserAccount user = UserAccount.of("qweeqw","asdfdf", userNickname,"asdf","asdf","M","하이요");
        given(userAccountRepository.findByNickname(userNickname)).willReturn(user);

        //when
        boolean result = userAccountService.checkNickname(userNickname);

        //then
        assertThat(result).isTrue();

    }

}