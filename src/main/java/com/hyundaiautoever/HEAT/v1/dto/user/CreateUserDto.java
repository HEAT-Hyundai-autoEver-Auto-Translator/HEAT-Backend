package com.hyundaiautoever.HEAT.v1.dto.user;

import lombok.Builder;
import lombok.Getter;
import org.checkerframework.checker.units.qual.N;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;
import org.jetbrains.annotations.NotNull;

@Getter
@Builder
public class CreateUserDto {

    @NotNull
    private String userEmail;
    @NotNull
//    @Length(min = 8)
    private String password;
    @NotNull
    private String userName;
    @NotNull
    private String profileImageUrl;
    @NotNull
    @Range(min = 1, max = 13)
    private Integer languageNo;
}
