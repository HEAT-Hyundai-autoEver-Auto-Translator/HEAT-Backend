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
        translationNo = translation.getTranslationNo();
        userId= translation.getUser().getUserId();
        requestLanguageName = translation.getRequestLanguageNo().getLanguageName();
        resultLanguageName = translation.getResultLanguageNo().getLanguageName();
        createDateTime = translation.getCreateDatetime();
        requestText = translation.getRequestText();
        resultText = translation.getResultText();
    }
}
