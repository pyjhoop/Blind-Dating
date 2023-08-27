package com.blind.dating.repository;

import com.blind.dating.domain.QUserAccount;
import com.blind.dating.domain.UserAccount;
import com.querydsl.core.types.dsl.DateTimeExpression;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.core.types.dsl.StringExpression;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;

import java.util.Optional;

public interface UserAccountRepository extends
        JpaRepository<UserAccount, Long>
, QuerydslPredicateExecutor<UserAccount>
, QuerydslBinderCustomizer<QUserAccount> {

    @Override
    default void customize(QuerydslBindings bindings, QUserAccount root){
        bindings.excludeUnlistedProperties(false); //기본은 false고 모든 요소에 대한 검색을 허용할 것인지다. true일 시 허용안함.
        //bindings.bind(root.title).first(StringExpression::likeIgnoreCase);     // like '{value}'
    };

    UserAccount findByUserId(String username);
    Boolean existsByUserId(String userId);
    UserAccount findByNickname(String nickname);

    Boolean existsByRefreshToken(String refreshToken);

    Optional<UserAccount> findByRefreshToken(String refreshToken);

//    @Query("SELECT u FROM UserAccount u " +
//            "LEFT JOIN u.likesUnlikes lu " +
//            "ON u.id = lu.receiverId " +
//            "WHERE u.gender = 'M' " +
//            "AND lu.userAccount.id = :userId")
//    Page<UserAccount> findAllByGenderAndNotUserId(String gender, Pageable pageable, Long userId);
//    //Page<UserAccount> findAllByGender(String gender, Pageable pageable);



}
