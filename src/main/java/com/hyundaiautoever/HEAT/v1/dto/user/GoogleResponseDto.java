package com.hyundaiautoever.HEAT.v1.dto.user;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Builder
@ToString
@EqualsAndHashCode
public class GoogleResponseDto {

    private String email;
    private String name;
    private String picture;
    private String locale;
}
