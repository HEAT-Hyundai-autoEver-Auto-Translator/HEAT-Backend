package com.hyundaiautoever.HEAT.v1.repository.translation;

import com.hyundaiautoever.HEAT.v1.dto.TranslationDto;
import com.hyundaiautoever.HEAT.v1.entity.QTranslation;
import com.hyundaiautoever.HEAT.v1.entity.Translation;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.stream.Collectors;

import static com.querydsl.jpa.JPAExpressions.selectFrom;

@Repository
@RequiredArgsConstructor
public class CustomTranslationRepositoryImpl implements CustomTranslationRepository{

    private final JPAQueryFactory queryFactory;

    @Transactional(readOnly = true)
    public List<Translation> findTranslationByUserId(String userId) {

        QTranslation translation = QTranslation.translation;

        // QueryDSL 쿼리 작성
        List<Translation> userTranslationList = queryFactory
                .selectFrom(translation)
                .join(translation.user)
                .where(translation.user.userId.eq(userId))
                .fetch();

        // 변환 후 반환
        return userTranslationList;
    }
}
