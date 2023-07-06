package com.hyundaiautoever.HEAT.v1.entity;

import java.time.LocalDate;
import java.util.Date;
import javax.persistence.*;

import com.hyundaiautoever.HEAT.v1.util.UserRole;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.checkerframework.common.aliasing.qual.Unique;
import org.jetbrains.annotations.NotNull;

@Entity
@Getter
@Setter
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
}
