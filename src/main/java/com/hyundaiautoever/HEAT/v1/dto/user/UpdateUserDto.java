package com.hyundaiautoever.HEAT.v1.dto.user;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateUserDto {

    private String userEmail;
    private String password;
    private String userName;
    private String profileImageUrl;
    private Integer languageNo;
}

