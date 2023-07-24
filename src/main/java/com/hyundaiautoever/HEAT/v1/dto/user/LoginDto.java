package com.hyundaiautoever.HEAT.v1.dto.user;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode
public class LoginDto {

    private String userEmail;
    private String userPassword;

    public LoginDto(){}
}
