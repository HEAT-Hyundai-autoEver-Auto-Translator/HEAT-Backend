package com.hyundaiautoever.HEAT.v1.dto.language;

import com.hyundaiautoever.HEAT.v1.entity.Language;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
@Schema
public class LanguageDto {

    @Schema(description = "언어 이름", example = "Korean")
    private String languageName;

    public LanguageDto(Language language) {
        this.languageName = language.getLanguageName();
    }
}
