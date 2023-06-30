package com.hyundaiautoever.HEAT.v1.dto;

import com.hyundaiautoever.HEAT.v1.entity.Language;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RequestTranslationDto {

    private Long userAccountNo;
    private String requestText;
    private Integer resultLanguageNo;
}
