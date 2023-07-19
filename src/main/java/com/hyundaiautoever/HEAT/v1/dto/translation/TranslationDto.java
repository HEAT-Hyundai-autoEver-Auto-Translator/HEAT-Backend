package com.hyundaiautoever.HEAT.v1.dto.translation;

import com.hyundaiautoever.HEAT.v1.entity.Translation;
import java.sql.Timestamp;

import io.swagger.annotations.ApiModel;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@EqualsAndHashCode
@Schema
public class TranslationDto {

    @Schema(description = "유저 인덱스", example = "1")
    private Long translationNo;
    @Schema(description = "유저 이메일", example = "example@example.com")
    private String userEmail;
    @Schema(description = "번역 요청 언어", example = "Korean")
    private String requestLanguageName;
    @Schema(description = "번역 결과 언어", example = "English")
    private String resultLanguageName;
    @Schema(description = "번역 요청 시간", example = "2023-07-11T10:58:37.250+00:00")
    private Timestamp createDateTime;
    @Schema(description = "번역 요청 텍스트", example = "안녕하세요. 번역 요청 드립니다.")
    private String requestText;
    @Schema(description = "번역 결과 텍스트", example = "Hello, I send a translation request")
    private String resultText;

    public TranslationDto(Translation translation){
        this.translationNo = translation.getTranslationNo();
        this.userEmail= translation.getUser().getUserEmail();
        this.requestLanguageName = translation.getRequestLanguage().getLanguageName();
        this.resultLanguageName = translation.getResultLanguage().getLanguageName();
        this.createDateTime = translation.getCreateDatetime();
        this.requestText = translation.getRequestText();
        this.resultText = translation.getResultText();
    }
}

