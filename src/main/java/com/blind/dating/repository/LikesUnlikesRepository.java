package com.blind.dating.repository;

import com.blind.dating.domain.LikesUnlikes;
import com.blind.dating.domain.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LikesUnlikesRepository extends JpaRepository<LikesUnlikes, Long> {

    Optional<LikesUnlikes> findByUserAccountAndReceiverId(UserAccount userAccount, Long receiverId);
}
