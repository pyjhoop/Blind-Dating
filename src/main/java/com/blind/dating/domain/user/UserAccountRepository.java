package com.blind.dating.domain.user;

import com.blind.dating.domain.user.UserAccount;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserAccountRepository extends JpaRepository<UserAccount, Long> {

    //나를 제외한 모든 유저 조회 n+1을 해결하기 위해 JPQL 작성
    Page<UserAccount> findAllByIdNot(Long id, Pageable pageable);

    //특정 성별을 제외한 유저 조회
    Page<UserAccount> findAllByIdNotAndGender(Long id, String gender, Pageable pageable);

    Optional<UserAccount> findByUserId(String userId);

    Boolean existsByUserId(String userId);

    Boolean existsByNickname(String nickname);

    Page<UserAccount> findAllByUserIdAndGender(String userId, String gender, Pageable pageable);

}