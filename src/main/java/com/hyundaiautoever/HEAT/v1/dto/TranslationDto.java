package com.hyundaiautoever.HEAT.v1.dto;

import com.hyundaiautoever.HEAT.v1.entity.Translation;
import java.sql.Timestamp;
import lombok.Builder;
import lombok.Getter;

@Getter
public class TranslationDto {

    private Long translationNo;
    private String userId;
    private String requestLanguageName;
    private String resultLanguageName;
    private Timestamp createDateTime;
    private String requestText;
    private String resultText;

    public TranslationDto(Translation translation){
        this.translationNo = translation.getTranslationNo();
        this.userId= translation.getUser().getUserId();
        this.requestLanguageName = translation.getRequestLanguage().getLanguageName();
        this.resultLanguageName = translation.getResultLanguage().getLanguageName();
        this.createDateTime = translation.getCreateDatetime();
        this.requestText = translation.getRequestText();
        this.resultText = translation.getResultText();
    }
}

