package com.blind.dating.domain.user;

import com.blind.dating.common.code.ResponseCode;
import com.blind.dating.domain.user.dto.*;
import com.blind.dating.common.Api;
import com.blind.dating.util.CookieUtil;
import com.blind.dating.common.code.UserResponseCode;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;


@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class UserAccountController {

    private final UserAccountService userAccountService;

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(
            @RequestBody @Valid UserRequestDto dto,
            BindingResult bindingResult
            ) throws MethodArgumentNotValidException {
        if (bindingResult.hasErrors()) {
            throw new MethodArgumentNotValidException((MethodParameter) null, bindingResult);
        }

        userAccountService.register(dto);
        return ResponseEntity.ok()
                .body(Api.OK(UserResponseCode.REGISTER_SUCCESS,null));
    }

    @PostMapping("/login")
    public ResponseEntity<Api<?>> authenticate(
            @RequestBody LoginInputDto dto,
            HttpServletResponse response
            ){

        LogInResponse user = userAccountService.getLoginInfo(dto.getUserId(), dto.getUserPassword());
        LogInResponseDto userInfo = LogInResponseDto.from(user);
        userInfo.setAccessToken(user.getAccessToken());

        CookieUtil.addCookie(response, "refreshToken", user.getRefreshToken());

        return ResponseEntity.ok()
                .body(Api.OK(UserResponseCode.LOGIN_SUCCESS, userInfo));
    }

    @PostMapping("/logout")
    public ResponseEntity<Api> logOut(
            @CookieValue(name = "refreshToken") Cookie cookie,
            HttpServletResponse response){

        cookie.setMaxAge(0);
        response.addCookie(cookie);

        return ResponseEntity.ok()
                .body(Api.OK(UserResponseCode.LOGOUT_SUCCESS, null));
    }

    @PostMapping("/check-email")
    public ResponseEntity<Api> checkUserId(@RequestBody UserIdRequestDto dto){

        UserResponseCode code = userAccountService.checkUserEmail(dto.getEmail());
        return ResponseEntity.ok()
                .body(Api.OK(code, code.getStatus()));
    }

    @PostMapping("/check-nickname")
    public ResponseEntity<?> checkNickname(@RequestBody UserNicknameRequestDto dto){
        ResponseCode code = userAccountService.checkNickname(dto.getNickname());

        return ResponseEntity.ok()
                .body(Api.OK(code, code.getStatus()));
    }

    




}
