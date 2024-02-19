package com.blind.dating.domain.interest;

import com.blind.dating.domain.interest.Interest;
import com.blind.dating.domain.user.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InterestRepository extends JpaRepository<Interest, Long> {

//    void deleteAllByUserAccount(UserAccount user);
    List<Interest> findAllByIdIn(List<Long> idList);
}
