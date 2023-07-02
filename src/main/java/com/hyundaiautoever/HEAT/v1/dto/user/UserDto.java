package com.hyundaiautoever.HEAT.v1.dto.user;

import java.time.LocalDate;
import java.util.Date;

import com.hyundaiautoever.HEAT.v1.entity.User;
import com.hyundaiautoever.HEAT.v1.util.UserRole;
import lombok.Builder;
import lombok.Getter;

@Getter
public class UserDto {

    private Long userAccountNo;
    private String userEmail;
    private String userName;
    private UserRole userRole;
    private String profileImageUrl;
    private String languageName;
    private LocalDate signupDate;
    private LocalDate lastAccessDate;

    public UserDto (User user) {
        userAccountNo = user.getUserAccountNo();
        userEmail = user.getUserEmail();
        userName = user.getUserName();
        userRole = user.getUserRole();
        profileImageUrl = user.getProfileImageUrl();
        languageName = user.getLanguage().getLanguageName();
        signupDate = user.getSignupDate();
        lastAccessDate = user.getLastAccessDate();
    }
}
