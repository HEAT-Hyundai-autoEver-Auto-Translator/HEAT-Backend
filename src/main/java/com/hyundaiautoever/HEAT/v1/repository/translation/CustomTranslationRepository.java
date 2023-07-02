package com.hyundaiautoever.HEAT.v1.repository.translation;

import com.hyundaiautoever.HEAT.v1.entity.Translation;

import java.util.List;

public interface CustomTranslationRepository {

   List<Translation> findTranslationByUserEmail(String userId);
}