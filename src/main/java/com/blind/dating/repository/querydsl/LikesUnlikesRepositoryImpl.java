package com.blind.dating.repository.querydsl;

import com.blind.dating.domain.LikesUnlikes;
import com.blind.dating.domain.QLikesUnlikes;
import com.blind.dating.domain.QUserAccount;
import com.blind.dating.domain.UserAccount;
import com.blind.dating.util.QueryDslUtil;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.springframework.util.ObjectUtils.isEmpty;


@Repository
public class LikesUnlikesRepositoryImpl extends QuerydslRepositorySupport implements LikesUnlikesRepositoryCustom {

    public LikesUnlikesRepositoryImpl() {
        super(LikesUnlikes.class);
    }
    @PersistenceContext
    private EntityManager entityManager;


    @Override
    public List<LikesUnlikes> findLikes(Long userId, UserAccount receiverId) {
        JPAQueryFactory queryFactory = new JPAQueryFactory(entityManager);
        QLikesUnlikes likesUnlikes = QLikesUnlikes.likesUnlikes;

        List<LikesUnlikes> likes = queryFactory
                .select(likesUnlikes)
                .from(likesUnlikes)
                .where(likesUnlikes.userId.eq(userId)
                        .and(likesUnlikes.receiver.eq(receiverId)))
                .fetch();
        return likes;
    }
}
