package com.hyundaiautoever.HEAT.v1.dto.user;

import lombok.*;
import org.jetbrains.annotations.NotNull;

@Getter
@Builder
@EqualsAndHashCode
public class UpdateUserDto {

    private Long userAccountNo;
    private String password;
    private String userName;
    private String languageName;
}

