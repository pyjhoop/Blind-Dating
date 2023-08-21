package com.blind.dating.repository.querydsl;

import com.blind.dating.domain.LikesUnlikes;
import com.blind.dating.domain.QLikesUnlikes;
import com.blind.dating.domain.QUserAccount;
import com.blind.dating.domain.UserAccount;
import com.blind.dating.util.QueryDslUtil;
import com.querydsl.core.QueryFactory;
import com.querydsl.core.QueryResults;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.SubQueryExpression;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.util.ObjectUtils.isEmpty;


@Repository
public class UserAccountRepositoryImpl extends QuerydslRepositorySupport implements UserAccountRepositoryCustom {

    public UserAccountRepositoryImpl() {
        super(UserAccount.class);
    }
    @PersistenceContext
    private EntityManager entityManager;


    @Override
    public Page<UserAccount> findAllByGenderAndNotLikes(String gender, Long userId, Pageable pageable) {
        JPAQueryFactory queryFactory = new JPAQueryFactory(entityManager);
        QUserAccount user = QUserAccount.userAccount;
        QLikesUnlikes likesUnlikes = QLikesUnlikes.likesUnlikes;
        List<OrderSpecifier> ORDERS = getAllOrderSpecifiers(pageable);

        JPQLQuery<LikesUnlikes> subquery = JPAExpressions
                .select(likesUnlikes)
                .from(likesUnlikes)
                .where(likesUnlikes.userId.eq(userId));


        List<UserAccount> content = queryFactory
                .select(user)
                .from(user)
                .leftJoin(user.likesUnlikes, likesUnlikes)
                .on(likesUnlikes.in(subquery))
                .where(
                   user.gender.eq(gender)
                           .and(likesUnlikes.userId.ne(userId).or(likesUnlikes.userId.isNull()))
                )
                .orderBy(user.recentLogin.desc())
                .offset(pageable.getOffset())   // (2) 페이지 번호
                .limit(pageable.getPageSize())
                .fetch();

        Long count = queryFactory
                .select(user.count())
                .from(user)
                .leftJoin(user.likesUnlikes, likesUnlikes)
                .on(likesUnlikes.in(subquery))
                .where(
                        user.gender.eq(gender)
                                .and(likesUnlikes.userId.ne(userId).or(likesUnlikes.userId.isNull()))
                )
                .fetchOne();



        return new PageImpl<>(content, pageable, count);

    }

    private List<OrderSpecifier> getAllOrderSpecifiers(Pageable pageable) {

        List<OrderSpecifier> ORDERS = new ArrayList<>();

        if (!isEmpty(pageable.getSort())) {
            for (Sort.Order order : pageable.getSort()) {
                Order direction = order.getDirection().isAscending() ? Order.ASC : Order.DESC;
                switch (order.getProperty()) {
                    case "recentLogin":
                        OrderSpecifier<?> orderId = QueryDslUtil.getSortedColumn(direction, QLikesUnlikes.likesUnlikes, "recentLogin");
                        ORDERS.add(orderId);
                        break;
                    case "user":
                        OrderSpecifier<?> orderUser = QueryDslUtil.getSortedColumn(direction, QLikesUnlikes.likesUnlikes, "name");
                        ORDERS.add(orderUser);
                        break;
                    case "category":
                        OrderSpecifier<?> orderCategory = QueryDslUtil.getSortedColumn(direction, QLikesUnlikes.likesUnlikes, "category");
                        ORDERS.add(orderCategory);
                        break;
                    default:
                        break;
                }
            }
        }

        return ORDERS;
    }


}
