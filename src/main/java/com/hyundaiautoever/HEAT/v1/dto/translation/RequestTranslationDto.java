package com.hyundaiautoever.HEAT.v1.dto.translation;

import com.hyundaiautoever.HEAT.v1.entity.Language;
import io.swagger.annotations.ApiModel;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;
import org.jetbrains.annotations.NotNull;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@Schema
public class RequestTranslationDto {

    @Schema(description = "유저 인덱스", example = "1")
    @Range(min = 1)
    private Long userAccountNo;
    @Schema(description = "번역 요청 텍스트", example = "안녕하세요 번역 요청 부탁드립니다.")
    @Length(min = 2, max = 2000)
    private String requestText;
    @Schema(description = "번역 결과 언어", example = "English")
    private String resultLanguageName;
}
