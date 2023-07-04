package com.hyundaiautoever.HEAT.v1.dto.user;

import lombok.Getter;
import lombok.Setter;

@Setter
public class LoginResponseDto {

    private String accessToken;
    private String refreshToken;
}
