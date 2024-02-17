package com.blind.dating.domain.interest;

import com.blind.dating.domain.interest.Interest;
import com.blind.dating.domain.user.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InterestRepository extends JpaRepository<Interest, Long> {

    void deleteAllByUserAccount(UserAccount user);
}
