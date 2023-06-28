package com.hyundaiautoever.HEAT.v1.repository;

import com.hyundaiautoever.HEAT.v1.entity.Language;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

public interface LanguageRepository extends JpaRepository<Language, Integer> {

    Language findByLanguageNo(Integer languageId);
    Language findByLanguageCode(String languageCode);
}
