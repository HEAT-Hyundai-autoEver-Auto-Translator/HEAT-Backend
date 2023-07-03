package com.hyundaiautoever.HEAT.v1.dto.user;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

@Getter
@Setter
public class UpdateUserDto {

    private Long userAccountNo;
    private String userEmail;
    private String password;
    private String userName;
//    private String profileImageUrl;
    private Integer languageNo;
}

