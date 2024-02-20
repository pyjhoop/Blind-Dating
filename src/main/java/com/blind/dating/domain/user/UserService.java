package com.blind.dating.domain.user;

import com.blind.dating.common.code.UserResponseCode;
import com.blind.dating.domain.interest.Interest;
import com.blind.dating.domain.user.dto.ProfileNames;
import com.blind.dating.domain.user.dto.UserInfo;
import com.blind.dating.dto.user.UserInfoDto;
import com.blind.dating.domain.user.dto.UserUpdateRequestDto;
import com.blind.dating.exception.ApiException;
import com.blind.dating.domain.interest.InterestRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
            Pageable pageable
    ){
        Page<UserAccount> users = userAccountRepository.findAllByDeleted(pageable, false);
        return users.map(UserInfoDto::From);
    }

    @Transactional(readOnly = true)
    public Page<UserInfoDto> getRecommendUsers(Pageable pageable, Authentication authentication) {
        // 내정보 조회후 관심사 조회
        Long userId = Long.valueOf((String) authentication.getPrincipal());
        UserAccount myEntity = userAccountRepository.findById(userId)
                .orElseThrow(()-> new ApiException(UserResponseCode.GET_USER_INFO_FAIL));

        List<Interest> interests = myEntity.getInterests();

        // 나와 관심사가 비슷한 사람들 조회
        String gender = (myEntity.getGender().equals("M"))? "W":"M";

        Page<UserAccount> users = userAccountRepository.findAllByGenderAndInterestsInAndDeleted(gender, interests, pageable, false);
        return users.map(UserInfoDto::From);
    }



    @Transactional(readOnly = true)
    public UserInfo getMyInfo(Authentication authentication){
        String userId = (String) authentication.getPrincipal();

        UserAccount user = userAccountRepository.findById(Long.valueOf(userId))
                .orElseThrow(()-> new ApiException(UserResponseCode.GET_USER_INFO_FAIL));

        return UserInfo.from(user);
    }

    @Transactional
    public UserAccount updateMyInfo(Authentication authentication, UserUpdateRequestDto dto){

        String userId = (String) authentication.getPrincipal();
        UserAccount user = userAccountRepository.findById(Long.valueOf(userId))
                .orElseThrow(() -> new ApiException(UserResponseCode.UPDATE_USER_INFO_FAIL));

        user.setRegion(dto.getRegion());
        user.setMbti(dto.getMbti());
        user.setSelfIntroduction(dto.getSelfIntroduction());


        List<Interest> interests = new ArrayList<>();
        for(Long s: dto.getInterests()){
            interests.add(Interest.of(s));
        }
        user.setInterests(interests);

        return user;
    }

    @Transactional
    public void deleteMe(Authentication authentication) {
        Long userId = Long.valueOf((String) authentication.getPrincipal());

        UserAccount user = userAccountRepository.findById(userId)
                .orElseThrow(()-> new ApiException(UserResponseCode.GET_USER_INFO_FAIL));

        user.setDeleted(true);
    }

    @Transactional(readOnly = true)
    public Optional<UserAccount> getUser(Long otherId) {
        return userAccountRepository.findById(otherId);
    }


    @Transactional
    public void updateProfile(Long userId, MultipartFile uploadFile, HttpServletRequest request) {
        // 파일 저장
        ProfileNames profileNames = saveProfile(uploadFile, request);
        System.out.println(">?>>>");
        UserAccount user = userAccountRepository.findById(userId)
                .orElseThrow(()-> new ApiException(UserResponseCode.UPDATE_PROFILE_FAIL));

        user.setOriginProfile(profileNames.originName());
        user.setChangedProfile(profileNames.saveName());
    }

    private ProfileNames saveProfile(MultipartFile uploadFile, HttpServletRequest request) {
        String originName;
        String saveName;
        System.out.println("originName");

        try{
            String path = "C:\\upload";
            File directory = new File(path);

            if(!directory.exists()) {
                System.out.println("여기 실행 안됨");
                directory.mkdirs();
            }

            originName = uploadFile.getOriginalFilename();
            saveName = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date())+originName;
            System.out.println(path+"/"+saveName);

            uploadFile.transferTo(new File(path+"/"+saveName));
        }catch (IOException e){
            throw new ApiException(UserResponseCode.UPDATE_PROFILE_FAIL);
        }

        return new ProfileNames(originName, saveName);
    }



}
