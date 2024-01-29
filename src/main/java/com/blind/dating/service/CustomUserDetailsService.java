package com.blind.dating.service;


import com.blind.dating.domain.CustomUserDetails;
import com.blind.dating.domain.UserAccount;
import com.blind.dating.repository.UserAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserAccountRepository userAccountRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        UserAccount user = userAccountRepository.findByUserId(username)
                .orElseThrow(()-> new RuntimeException("존재하지 않는 유저입니다."));

        return new CustomUserDetails(user);
    }
}
