package com.hyundaiautoever.HEAT.v1.dto.user;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
public class GoogleResponseDto {

    private String email;
    private String displayName;
    private String language;
    private String photoUrl;
}
