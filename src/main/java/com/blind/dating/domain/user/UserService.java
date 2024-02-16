package com.blind.dating.domain.user;

import com.blind.dating.common.code.UserResponseCode;
import com.blind.dating.domain.interest.Interest;
import com.blind.dating.dto.user.UserInfoDto;
import com.blind.dating.dto.user.UserUpdateRequestDto;
import com.blind.dating.dto.user.UserWithInterestAndQuestionDto;
import com.blind.dating.exception.ApiException;
import com.blind.dating.domain.interest.InterestRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
@Slf4j
public class UserService {

    private final UserAccountRepository userAccountRepository;
    private final InterestRepository interestRepository;
    // 최근 로그인 순으로 나를 제외한 전체 유저 30명 조회
    @Transactional(readOnly = true)
    public Page<UserInfoDto> getMaleAndFemaleUsers(
            Pageable pageable,
            Authentication authentication
    ){
        Long userId = Long.valueOf((String) authentication.getPrincipal());
        Page<UserAccount> users = userAccountRepository.findAllByIdNot(userId, pageable);
        return users.map(UserInfoDto::From);
    }
    // 최근 로그인 순으로 나를 제외한 남성 유저 30명 조회
    @Transactional(readOnly = true)
    public Page<UserInfoDto> getMaleUsers(
            Pageable pageable,
            Authentication authentication
    ){
        Long userId = Long.valueOf((String) authentication.getPrincipal());
        Page<UserAccount> users = userAccountRepository.findAllByIdNotAndGender(userId, "M" ,pageable);
        return users.map(UserInfoDto::From);
    }
    // 최근 로그인 순으로 나를 제외한 여성 유저 30명 조회
    @Transactional(readOnly = true)
    public Page<UserInfoDto> getFemaleUsers(
            Pageable pageable,
            Authentication authentication
    ){
        Long userId = Long.valueOf((String) authentication.getPrincipal());
        Page<UserAccount> users = userAccountRepository.findAllByIdNotAndGender(userId, "W" ,pageable);
        return users.map(UserInfoDto::From);
    }


    public UserWithInterestAndQuestionDto getMyInfo(Authentication authentication){
        String userId = (String) authentication.getPrincipal();

        UserAccount user = userAccountRepository.findById(Long.valueOf(userId))
                .orElseThrow(()-> new ApiException(UserResponseCode.GET_USER_INFO_FAIL));

        return UserWithInterestAndQuestionDto.from(user);
    }

    @Transactional
    public UserAccount updateMyInfo(Authentication authentication, UserUpdateRequestDto dto){

        String userId = (String) authentication.getPrincipal();

        UserAccount user = userAccountRepository.findById(Long.valueOf(userId))
                .orElseThrow(() -> new ApiException(UserResponseCode.UPDATE_USER_INFO_FAIL));
        // 유저 정보 저장하기
        user.setRegion(dto.getRegion());
        user.setMbti(dto.getMbti());
        user.setSelfIntroduction(dto.getSelfIntroduction());

        interestRepository.deleteAllByUserAccount(user);

        List<Interest> interests = new ArrayList<>();
        for(String s: dto.getInterests()){
            interests.add(Interest.of(user,s));
        }
        user.setInterests(interests);

        return user;
    }

    public Optional<UserAccount> getUser(Long otherId) {
        return userAccountRepository.findById(otherId);
    }
}
