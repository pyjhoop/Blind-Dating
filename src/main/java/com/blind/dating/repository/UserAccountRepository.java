package com.blind.dating.repository;

import com.blind.dating.domain.UserAccount;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserAccountRepository extends JpaRepository<UserAccount, Long> {

    Optional<UserAccount> findByUserId(String userId);
    Boolean existsByUserId(String userId);
    UserAccount findByNickname(String nickname);

    Page<UserAccount> findAllByUserIdAndGender(String userId, String gender, Pageable pageable);

    @Query("SELECT DISTINCT a FROM UserAccount a left join LikesUnlikes u on (a.id = u.userId)" +
            "WHERE a.gender=:gender AND a.id not in (SELECT ul.receiver.id FROM LikesUnlikes ul WHERE ul.userId = :userId)")
    Page<UserAccount> recommendUser(String gender, Long userId, Pageable pageable);

}
