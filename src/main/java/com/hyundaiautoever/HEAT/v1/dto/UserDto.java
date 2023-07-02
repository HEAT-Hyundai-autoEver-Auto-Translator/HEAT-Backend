package com.hyundaiautoever.HEAT.v1.dto;

import java.util.Date;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserDto {

    private String userId;
    private String userName;
    private String userRole;
    private String imageUrl;
    private String languageNo;
    private Date signupDate;
    private Date lastAccessDate;
}
