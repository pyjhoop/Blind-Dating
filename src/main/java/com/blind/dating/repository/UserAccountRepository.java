package com.blind.dating.repository;

import com.blind.dating.domain.UserAccount;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserAccountRepository extends JpaRepository<UserAccount, Long> {
    UserAccount findByUserId(String username);
    Boolean existsByUserId(String userId);
    UserAccount findByNickname(String nickname);
    Page<UserAccount> findAllByScoreBetweenAndGender(int minScore, int maxScore,String gender, Pageable pageable);

}
