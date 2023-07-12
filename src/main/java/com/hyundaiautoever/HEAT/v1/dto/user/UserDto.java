package com.hyundaiautoever.HEAT.v1.dto.user;

import java.time.LocalDate;
import java.util.Date;

import com.hyundaiautoever.HEAT.v1.entity.User;
import com.hyundaiautoever.HEAT.v1.util.UserRole;
import io.swagger.annotations.ApiModel;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Schema
public class UserDto {

    @Schema(description = "유저 인덱스", example = "1")
    private Long userAccountNo;
    @Schema(description = "유저 이메일", example = "example@exmaple.com")
    private String userEmail;
    @Schema(description = "유저 이름", example = "testUSer1")
    private String userName;
    @Schema(description = "유저 권한", example = "user")
    private UserRole userRole;
    @Schema(description = "유저 프로필 이미지 url", example = "https://heat-back-s3.s3...4638ef0ec3/image.png")
    private String profileImageUrl;
    @Schema(description = "유저 기본 설정 언어", example = "English")
    private String languageName;
    @Schema(description = "회원가입 일자", example = "2023-07-10")
    private LocalDate signupDate;
    @Schema(description = "마지막 로그인 일자", example = "2023-07-10")
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
