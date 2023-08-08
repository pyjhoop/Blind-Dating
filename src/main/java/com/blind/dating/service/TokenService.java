package com.blind.dating.service;

import com.blind.dating.domain.UserAccount;
import com.blind.dating.repository.UserAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class TokenService {

    private final UserAccountRepository userAccountRepository;

    public boolean validRefreshToken(String refreshToken, UserAccount userAccount){

        UserAccount user = userAccountRepository.findByUserId(userAccount.getUserId());

        return refreshToken.equals(user.getRefreshToken());

    }

    @Transactional
    public void updateRefreshToken(String newRefreshToken, UserAccount userAccount){
        UserAccount user = userAccountRepository.findByUserId(userAccount.getUserId());
        user.setRefreshToken(newRefreshToken);

    }


}
