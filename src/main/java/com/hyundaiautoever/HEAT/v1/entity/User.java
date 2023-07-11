package com.hyundaiautoever.HEAT.v1.entity;

import java.time.LocalDate;
import java.util.Date;
import javax.persistence.*;

import com.hyundaiautoever.HEAT.v1.util.S3Util;
import com.hyundaiautoever.HEAT.v1.util.UserRole;
import lombok.*;
import org.checkerframework.common.aliasing.qual.Unique;
import org.jetbrains.annotations.NotNull;
import org.springframework.util.StringUtils;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "user", indexes = @Index(name = "idx_user_email", columnList = "user_email"))
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userAccountNo;

    @Column(name = "user_email", unique = true, nullable = false)
    private String userEmail;
    private String passwordHash;
    @Column(nullable = false)
    private String userName;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole userRole;
    private String profileImageUrl;
    private String refreshToken;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "language_no", nullable = false)
    private Language language;

    @Column(nullable = false)
    private LocalDate signupDate;
    private LocalDate lastAccessDate;

    @Builder
    public User(Long userAccountNo, String userEmail, String passwordHash, String userName, UserRole userRole, String profileImageUrl, String refreshToken, Language language, LocalDate signupDate, LocalDate lastAccessDate) {
        this.userEmail = userEmail;
        this.passwordHash = passwordHash;
        this.userName = userName;
        this.userRole = userRole;
        this.profileImageUrl = profileImageUrl;
        this.refreshToken = refreshToken;
        this.language = language;
        this.signupDate = signupDate;
        this.lastAccessDate = lastAccessDate;
    }

    @Builder(builderMethodName = "updateBuilder")
    public void updateUser(String passwordHash, String userName, String profileImageUrl, Language language) {
        if (StringUtils.hasText(passwordHash)) {
            this.passwordHash = passwordHash;
        }
        if (StringUtils.hasText(userName)) {
            this.userName = userName;
        }
        if (StringUtils.hasText(profileImageUrl)) {
            this.profileImageUrl = profileImageUrl;
        }
        if (language != null) {
            this.language = language;
        }
    }

    public void updateUserRole(UserRole userRole) {
        this.userRole = userRole;
    }

    public void updateLogin(String refreshToken, LocalDate lastAccessDate) {
        this.refreshToken = refreshToken;
        this.lastAccessDate = lastAccessDate;
    }
}
