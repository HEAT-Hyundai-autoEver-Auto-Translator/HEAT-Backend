package com.hyundaiautoever.HEAT.v1.util;

import com.hyundaiautoever.HEAT.v1.dto.translation.TranslationDto;
import com.hyundaiautoever.HEAT.v1.entity.Language;
import com.hyundaiautoever.HEAT.v1.entity.Translation;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper
public interface TranslationMapper {

    TranslationDto toTranslationDto(Translation translation);

    default String toRequestLanguageName(Language language) {
        return language.getLanguageName();
    }

    default String toResultLanguageName(Language language) {
        return language.getLanguageName();
    }

    List<TranslationDto> toTranslationDtoList(List<Translation> translationList);
}
