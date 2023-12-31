package com.hyundaiautoever.HEAT.v1.repository;

import com.hyundaiautoever.HEAT.v1.entity.Language;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

public interface LanguageRepository extends JpaRepository<Language, Integer> {

    Language findByLanguageNo(Integer languageNo);
    Language findByLanguageName(String languageName);
    Language findByLanguageCode(String languageCode);
    List<Language> findAll();
}
