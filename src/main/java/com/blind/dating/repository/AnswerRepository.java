package com.blind.dating.repository;

import com.blind.dating.domain.Answer;
import com.blind.dating.domain.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AnswerRepository extends JpaRepository<Answer, Long> {

    List<Answer> findAllByUserAccountId(Long userAccountId);
}
