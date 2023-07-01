package com.hyundaiautoever.HEAT.v1.repository.translation;

import com.hyundaiautoever.HEAT.v1.dto.TranslationDto;
import com.hyundaiautoever.HEAT.v1.entity.Translation;
import com.hyundaiautoever.HEAT.v1.entity.User;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TranslationRepository extends JpaRepository<Translation, Long>, CustomTranslationRepository {

    Translation save(Translation translation);
    Translation findByTranslationNo(Long translationNo);
    List<Translation> findTranslationByUserId(String userId);
}
