package com.hyundaiautoever.HEAT.v1.repository.translation;

import com.hyundaiautoever.HEAT.v1.entity.QTranslation;
import com.hyundaiautoever.HEAT.v1.entity.Translation;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class CustomTranslationRepositoryImpl implements CustomTranslationRepository{

    private final JPAQueryFactory queryFactory;

    @Transactional(readOnly = true)
    public List<Translation> findTranslationByUserEmail(String userEmail) {

        QTranslation translation = QTranslation.translation;

        // QueryDSL 쿼리 작성
        List<Translation> userTranslationList = queryFactory
                .selectFrom(translation)
                .join(translation.user)
                .where(translation.user.userEmail.eq(userEmail))
                .fetch();

        return userTranslationList;
    }
}
