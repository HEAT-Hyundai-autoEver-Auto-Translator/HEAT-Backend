package com.hyundaiautoever.HEAT.v1.dto.user;

import lombok.*;
import org.checkerframework.checker.units.qual.N;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;
import org.jetbrains.annotations.NotNull;

@Getter
@Builder
@EqualsAndHashCode
public class CreateUserDto {

    @NotNull
    private String userEmail;
    @NotNull
//    @Length(min = 8)
    private String password;
    @NotNull
    private String userName;
    @NotNull
    private String languageName;
}
