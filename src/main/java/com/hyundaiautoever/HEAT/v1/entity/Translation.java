package com.hyundaiautoever.HEAT.v1.entity;

import java.sql.Timestamp;
import javax.persistence.*;
import javax.ws.rs.CookieParam;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

@Entity
@Getter
@Setter
@Table(name = "translation", indexes = @Index(name = "idx_user_account_no", columnList = "user_account_no"))
public class Translation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long translationNo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_account_no", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "request_language_no", nullable = false)
    private Language requestLanguage;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "result_language_no", nullable = false)
    private Language resultLanguage;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String requestText;
    @Column(columnDefinition = "TEXT", nullable = false)
    private String resultText;
    @Column(nullable = false)
    private Timestamp createDatetime;
}
