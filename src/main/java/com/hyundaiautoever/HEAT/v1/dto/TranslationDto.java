package com.hyundaiautoever.HEAT.v1.dto;

import java.sql.Timestamp;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TranslationDto {

    private Long translationNo;
    private String userId;
    private String requestLanguageName;
    private String resultLanguageName;
    private Timestamp createDateTime;
    private String requestText;
    private String resultText;
}
