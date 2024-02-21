package com.blind.dating.domain.user;

import com.blind.dating.domain.interest.Interest;
import com.blind.dating.domain.user.UserAccount;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserAccountRepository extends JpaRepository<UserAccount, Long> {

    //나를 제외한 모든 유저 조회 n+1을 해결하기 위해 JPQL 작성
    Page<UserAccount> findAllByDeleted(Pageable pageable, Boolean status);

    //특정 성별을 제외한 유저 조회
    Page<UserAccount> findAllByIdNotAndGender(Long id, String gender, Pageable pageable);

    Optional<UserAccount> findByEmail(String email);

    Boolean existsByEmail(String email);

    Boolean existsByNickname(String nickname);

    Page<UserAccount> findAllByGenderAndInterestsInAndDeleted(String gender, List<Interest> interests, Pageable pageable, Boolean status);


}
