package com.hyundaiautoever.HEAT.v1.repository.translation;

import com.hyundaiautoever.HEAT.v1.dto.TranslationDto;
import com.hyundaiautoever.HEAT.v1.entity.Translation;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface CustomTranslationRepository {

   List<Translation> findTranslationByUserId(String userId);
}