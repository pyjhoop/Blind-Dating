package com.blind.dating.service;

import com.blind.dating.common.code.UserResponseCode;
import com.blind.dating.domain.interest.Interest;
import com.blind.dating.domain.user.UserAccount;
import com.blind.dating.domain.user.UserService;
import com.blind.dating.domain.user.dto.UserInfo;
import com.blind.dating.dto.user.UserInfoDto;
import com.blind.dating.domain.user.dto.UserUpdateRequestDto;
import com.blind.dating.exception.ApiException;
import com.blind.dating.domain.interest.InterestRepository;
import com.blind.dating.domain.user.UserAccountRedisRepository;
import com.blind.dating.domain.user.UserAccountRepository;
import jakarta.servlet.ServletContext;
import org.junit.jupiter.api.BeforeEach;
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
import org.springframework.security.test.context.support.WithMockUser;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;


@DisplayName("UserService 테스트")
@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @InjectMocks private UserService userService;
    @Mock private UserAccountRepository userAccountRepository;
    @Mock private InterestRepository interestRepository;
    @Mock
    private ServletContext servletContext;

    @Mock private UserAccountRedisRepository userAccountRedisRepository;

    private UserAccount user;
    private UserAccount user2;
    private Authentication authentication;

    @BeforeEach
    void setUp(){
        user = UserAccount.of("user01@gmail.com","pass01", "nickname1","서울","intp","M","하이요");
        user.setInterests(List.of(new Interest()));
        user2 = UserAccount.of("user02","pass02", "nickname2","서울","intp","W","하이요");
        user2.setInterests(List.of(new Interest()));
        authentication = new UsernamePasswordAuthenticationToken("1",user.getUserPassword());
    }

    @DisplayName("성별 상관없이 유저 조회")
    @Test
    @WithMockUser(username = "1")
    void givenId_whenGetMaleAndFemaleUsers_thenReturnUsers() {
        // Given
        Long id = 1L;
        Pageable pageable = Pageable.ofSize(2);
        Page<UserAccount> page = new PageImpl<>(List.of(user,user2));
        given(userAccountRepository.findAllByDeleted(pageable, false)).willReturn(page);

        // When
        Page<UserInfoDto> result = userService.getMaleAndFemaleUsers(pageable);

        // Then
        assertThat(result.getContent().get(0)).hasFieldOrPropertyWithValue("nickname",user.getNickname());
        assertThat(result.getContent().get(1)).hasFieldOrPropertyWithValue("nickname",user2.getNickname());
    }


    @DisplayName("내정보 조회 성공")
    @Test
    @WithMockUser(username = "1")
    void givenUser_whenGetMyInfo_thenReturnUserInfo(){
        //Given
        given(userAccountRepository.findById(1L)).willReturn(Optional.of(user));

        //When
        UserInfo result = userService.getMyInfo(authentication);

        //Then
        assertThat(result).isNotNull();
    }

    @DisplayName("내정보 조회시 예외발생")
    @Test
    @WithMockUser(username = "1")
    void givenUser_whenGetMyInfo_thenThrowException(){
        //Given
        given(userAccountRepository.findById(1L)).willReturn(Optional.empty());

        //When
        ApiException exception = assertThrows(ApiException.class, () -> {
            userService.getMyInfo(authentication);
        });

        //Then
        assertThat(exception.getResponseCode()).isEqualTo(UserResponseCode.GET_USER_INFO_FAIL);
    }



    @DisplayName("내 정보 수정하기 성공")
    @Test
    @WithMockUser(username = "1")
    void givenUser_whenUpdateMyInfo_thenReturnUserInfo(){
        //Given
        Optional<UserAccount> opUser = Optional.of(user);
        List<Long> list = List.of(1L, 2L);
        UserUpdateRequestDto dto = UserUpdateRequestDto.builder()
                .region("인천")
                .mbti("MBTI")
                .selfIntroduction("하이요")
                .interests(list).build();

        given(userAccountRepository.findById(1L)).willReturn(opUser);

        //When
        UserAccount result = userService.updateMyInfo(authentication, dto);

        //Then
        assertThat(result).isNotNull();
        assertThat(result).hasFieldOrPropertyWithValue("email",user.getEmail());
    }

    @DisplayName("내 정보 수정시 예외발생")
    @Test
    @WithMockUser(username = "1")
    void givenUser_whenUpdateMyInfo_thenThrowException(){
        //Given
        given(userAccountRepository.findById(1L)).willReturn(Optional.empty());
        UserUpdateRequestDto dto = new UserUpdateRequestDto();

        //When
        ApiException exception = assertThrows(ApiException.class, () -> {
            UserAccount result = userService.updateMyInfo(authentication, dto);
        });

        //Then
        assertThat(exception.getResponseCode()).isEqualTo(UserResponseCode.UPDATE_USER_INFO_FAIL);
    }


    @DisplayName("특정 유저 조회 성공")
    @Test
    @WithMockUser(username = "1")
    void givenUserId_whenSelectUserAccount_thenReturnUserAccount(){
        //Given
        Optional<UserAccount> opUser = Optional.of(user);
        given(userAccountRepository.findById(1L)).willReturn(opUser);

        //When
        Optional<UserAccount> result = userService.getUser(1L);

        //Then
        assertThat(result).isNotNull();
        assertThat(result.get()).hasFieldOrPropertyWithValue("email",user.getEmail());
    }




}
