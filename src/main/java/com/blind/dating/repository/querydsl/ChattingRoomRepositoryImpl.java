package com.blind.dating.repository.querydsl;

import com.blind.dating.domain.*;
import com.blind.dating.dto.chat.ChatRoomDto;
import com.blind.dating.util.QueryDslUtil;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.util.ObjectUtils.isEmpty;


@Repository
public class ChattingRoomRepositoryImpl extends QuerydslRepositorySupport implements ChattingRoomRepositoryCustom {

    public ChattingRoomRepositoryImpl() {
        super(ChatRoom.class);
    }
    @PersistenceContext
    private EntityManager entityManager;


    @Override
    public List<ChatRoom> findAllByUserId(Long userId1) {
        JPAQueryFactory queryFactory = new JPAQueryFactory(entityManager);
        QChatRoom chatRoom = QChatRoom.chatRoom;

        List<ChatRoom> content = queryFactory.select(chatRoom)
                .from(chatRoom)
                .where(chatRoom.user1.id.eq(userId1).or(chatRoom.user2.id.eq(userId1)))
                .fetch();

        return content;
    }









    private List<OrderSpecifier> getAllOrderSpecifiers(Pageable pageable) {

        List<OrderSpecifier> ORDERS = new ArrayList<>();

        if (!isEmpty(pageable.getSort())) {
            for (Sort.Order order : pageable.getSort()) {
                Order direction = order.getDirection().isAscending() ? Order.ASC : Order.DESC;
                switch (order.getProperty()) {
                    case "updatedAt":
                        OrderSpecifier<?> orderId = QueryDslUtil.getSortedColumn(direction, QChatRoom.chatRoom, "updatedAt");
                        ORDERS.add(orderId);
                        break;
                    case "user":
                        OrderSpecifier<?> orderUser = QueryDslUtil.getSortedColumn(direction, QChatRoom.chatRoom, "name");
                        ORDERS.add(orderUser);
                        break;
                    case "category":
                        OrderSpecifier<?> orderCategory = QueryDslUtil.getSortedColumn(direction, QChatRoom.chatRoom, "category");
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
