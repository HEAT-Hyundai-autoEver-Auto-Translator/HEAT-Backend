package com.hyundaiautoever.HEAT.v1.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CreateUserDto {

    private String userId;
    private String password;
    private String userName;
    private String profileImageUrl;
    private Integer languageNo;
}
