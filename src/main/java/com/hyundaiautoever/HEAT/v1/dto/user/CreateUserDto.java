package com.hyundaiautoever.HEAT.v1.dto.user;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CreateUserDto {

    private String userEmail;
    private String password;
    private String userName;
    private String profileImageUrl;
    private Integer languageNo;
}
