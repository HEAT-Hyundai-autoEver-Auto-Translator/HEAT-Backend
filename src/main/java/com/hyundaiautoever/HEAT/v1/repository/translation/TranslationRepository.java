package com.hyundaiautoever.HEAT.v1.repository.translation;

import com.hyundaiautoever.HEAT.v1.entity.Translation;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TranslationRepository extends JpaRepository<Translation, Long>, CustomTranslationRepository {

    Translation save(Translation translation);
    Optional<Translation> findByTranslationNo(Long translationNo);
    List<Translation> findTranslationByUserEmail(String userId);
}
