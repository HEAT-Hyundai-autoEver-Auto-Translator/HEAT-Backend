package com.hyundaiautoever.HEAT.v1.dto.translation;

import com.hyundaiautoever.HEAT.v1.entity.Language;
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
public class RequestTranslationDto {

    @Range(min = 1)
    private Long userAccountNo;
    @Length(min = 2, max = 2000)
    private String requestText;
    private String resultLanguageName;
}
