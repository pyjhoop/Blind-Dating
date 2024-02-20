package com.blind.dating.domain.user;


import com.blind.dating.domain.user.CustomUserDetails;
import com.blind.dating.domain.user.UserAccount;
import com.blind.dating.domain.user.UserAccountRepository;
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
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        UserAccount user = userAccountRepository.findByEmail(email)
                .orElseThrow(()-> new RuntimeException("존재하지 않는 유저입니다."));

        return new CustomUserDetails(user);
    }
}
