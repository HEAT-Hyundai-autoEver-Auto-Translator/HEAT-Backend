package com.hyundaiautoever.HEAT.v1.repository.user;

import com.hyundaiautoever.HEAT.v1.entity.QUser;
import com.hyundaiautoever.HEAT.v1.entity.User;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class CustomUserRepositoryImpl implements CustomUserRepository{

    private final JPAQueryFactory queryFactory;

    @Transactional(readOnly = true)
    public List<User> findUserByUserName(String userName) {

        QUser user = QUser.user;

        //QueryDSL 쿼리 작성
        List<User> userFoundList = queryFactory
                .selectFrom(user)
                .where(user.userName.like(userName + "%"))
                .fetch();

        return userFoundList;
    }
}
