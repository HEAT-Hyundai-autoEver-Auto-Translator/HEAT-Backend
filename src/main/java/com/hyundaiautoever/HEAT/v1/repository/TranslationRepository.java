package com.hyundaiautoever.HEAT.v1.repository;

import com.hyundaiautoever.HEAT.v1.entity.Translation;
import com.hyundaiautoever.HEAT.v1.entity.User;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TranslationRepository extends JpaRepository<Translation, Long> {

    List<Translation> findByUser(User user);

    List<Translation> findByRequestLanguageNo(Integer requestLanguageNo);

    List<Translation> findByResultLanguageNo(Integer resultLanguageNo);

    Translation save(Translation translation);
}
