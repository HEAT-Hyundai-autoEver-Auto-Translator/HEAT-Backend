package com.hyundaiautoever.HEAT.v1.dto.user;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class GoogleResponseDto {

    private String email;
    private String name;
    private String picture;
    private String locale;
}
