package com.blind.dating.domain.user;

import com.blind.dating.common.code.UserResponseCode;
import com.blind.dating.common.Api;
import com.blind.dating.domain.user.dto.UserInfo;
import com.blind.dating.dto.user.UserInfoDto;
import com.blind.dating.dto.user.UserInfoWithPageInfo;
import com.blind.dating.domain.user.dto.UserUpdateRequestDto;
import com.blind.dating.dto.user.UserWithInterestAndQuestionDto;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class UserController {

    private final UserService userService;

    @GetMapping("/users/recommend")
    public ResponseEntity<?> getRecommendUsers(
            Authentication authentication,
            @PageableDefault(size = 30, sort = "recentLogin", direction = Sort.Direction.DESC)
            Pageable pageable
    ) {
        Page<UserInfoDto> usersInfo = userService.getRecommendUsers(pageable, authentication);
        UserInfoWithPageInfo result = new UserInfoWithPageInfo(usersInfo.getNumber(), usersInfo.getTotalPages(), usersInfo.getContent());

        return ResponseEntity.ok()
                .body(Api.OK(UserResponseCode.GET_USER_LIST_SUCCESS, result));
    }

    @GetMapping("/users/all")
    public ResponseEntity<?> getMaleAndFemaleUsers(
            @PageableDefault(size = 30, sort = "recentLogin", direction = Sort.Direction.DESC)
            Pageable pageable
    ){
        Page<UserInfoDto> usersInfo = userService.getMaleAndFemaleUsers(pageable);
        UserInfoWithPageInfo result = new UserInfoWithPageInfo(usersInfo.getNumber(), usersInfo.getTotalPages(), usersInfo.getContent());

        return ResponseEntity.ok()
                .body(Api.OK(UserResponseCode.GET_USER_LIST_SUCCESS, result));
    }


    @GetMapping("/users/me")
    public ResponseEntity<?> getMyInfo(
            Authentication authentication
    ){
        // 내정보 조회하기
        UserInfo userInfo = userService.getMyInfo(authentication);

        return ResponseEntity.ok()
                .body(Api.OK(UserResponseCode.UPDATE_USER_INFO_SUCCESS, userInfo));
    }

    @PutMapping("/users")
    public ResponseEntity<Api<?>> updateMyInfo(
            Authentication authentication
            , @RequestBody UserUpdateRequestDto dto
    ){
        //내정보 업데이트
        UserAccount user = userService.updateMyInfo(authentication, dto);
        // 업데이트 된 정보 가져오기
        UserWithInterestAndQuestionDto userInfo = UserWithInterestAndQuestionDto.from(user);

        return ResponseEntity.ok()
                .body(Api.OK(UserResponseCode.UPDATE_USER_INFO_SUCCESS, userInfo));
    }

    @DeleteMapping("/users")
    public ResponseEntity<?> deleteUser(
            Authentication authentication
    ) {
        userService.deleteMe(authentication);

        return ResponseEntity.ok()
                .body(Api.OK(UserResponseCode.USER_DELETE_SUCCESS));
    }

    @PostMapping("/users/{userId}/profile")
    public ResponseEntity<?> updateProfile(
            @RequestParam("uploadFile") MultipartFile uploadFile,
            @PathVariable Long userId,
            HttpServletRequest request
    ) {
        userService.updateProfile(userId, uploadFile, request);

        return ResponseEntity.ok()
                .body(Api.OK(UserResponseCode.UPDATE_PROFILE_SUCCESS));
    }

}
