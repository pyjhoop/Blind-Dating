package com.blind.dating.repository.querydsl;

import com.blind.dating.domain.LikesUnlikes;
import com.blind.dating.domain.UserAccount;


import java.util.List;
import java.util.Optional;

public interface LikesUnlikesRepositoryCustom {

    List<LikesUnlikes> findLikes(Long userId, UserAccount receiverId);

}
