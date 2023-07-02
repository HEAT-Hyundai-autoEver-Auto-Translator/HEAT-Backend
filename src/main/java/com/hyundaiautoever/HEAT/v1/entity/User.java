package com.hyundaiautoever.HEAT.v1.entity;

import java.util.Date;
import javax.persistence.*;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "user", indexes = @Index(name = "idx_user_email", columnList = "user_email"))
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userAccountNo;

    @Column(name = "user_email")
    private String userEmail;
    private String passwordHash;
    private String userName;
    private String userRole;
    private String profileImageUrl;
    private String refreshToken;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "language_no")
    private Language languageNo;

    private Date signupDate;
    private Date lastAccessDate;
}
