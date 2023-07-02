package com.hyundaiautoever.HEAT.v1.dto.user;

import java.util.Date;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserDto {

    private Long userAccountNo;
    private String userEmail;
    private String userName;
    private String userRole;
    private String imageUrl;
    private String languageName;
    private Date signupDate;
    private Date lastAccessDate;
}
