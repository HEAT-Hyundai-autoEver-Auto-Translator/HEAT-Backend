package com.hyundaiautoever.HEAT.v1.entity;

import java.sql.Timestamp;
import javax.persistence.*;
import javax.ws.rs.CookieParam;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "translation", indexes = @Index(name = "idx_user_account_no", columnList = "user_account_no"))
public class Translation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long translationNo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_account_no")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "request_language_no")
    private Language requestLanguage;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "result_language_no")
    private Language resultLanguage;

    @Column(columnDefinition = "TEXT")
    private String requestText;
    @Column(columnDefinition = "TEXT")
    private String resultText;
    private Timestamp createDatetime;
}
