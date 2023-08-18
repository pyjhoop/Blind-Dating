package com.blind.dating.repository.querydsl;

import com.blind.dating.domain.UserAccount;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UserAccountRepositoryCustom {

    Page<UserAccount> findAllByGenderAndNotLikes(String gender, Long userId, Pageable pageable);

}
