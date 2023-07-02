package com.hyundaiautoever.HEAT.v1.dto.language;

import com.hyundaiautoever.HEAT.v1.entity.Language;
import lombok.Getter;

@Getter
public class LanguageDto {

    private String languageName;

    public LanguageDto(Language language) {
        this.languageName = language.getLanguageName();
    }
}
