package com.hyundaiautoever.HEAT.v1.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RequestTranslationDto {

    private Long userAccountNo;
    private String requestText;
    private Integer requestLanguageNo;
}
