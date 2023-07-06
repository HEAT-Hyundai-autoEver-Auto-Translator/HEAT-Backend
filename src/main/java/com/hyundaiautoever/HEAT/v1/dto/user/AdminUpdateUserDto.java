package com.hyundaiautoever.HEAT.v1.dto.user;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminUpdateUserDto {

    private Long userAccountNo;
    private String userEmail;
    private String password;
    private String userName;
    private String userRole;
    private String languageName;
}

