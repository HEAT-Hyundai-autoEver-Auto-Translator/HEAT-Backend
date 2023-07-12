package com.hyundaiautoever.HEAT.v1.entity;

import java.sql.Timestamp;
import javax.persistence.*;
import javax.ws.rs.CookieParam;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

@Entity
@Getter
@NoArgsConstructor
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
    @Column(columnDefinition = "TEXT")
    private String resultText;
    @Column(nullable = false)
    private Timestamp createDatetime;

    @Builder
    public Translation(Long translationNo, User user, Language requestLanguage, Language resultLanguage, String requestText, String resultText, Timestamp createDatetime) {
        this.user = user;
        this.requestLanguage = requestLanguage;
        this.resultLanguage = resultLanguage;
        this.requestText = requestText;
        this.resultText = resultText;
        this.createDatetime = createDatetime;
    }

    public void setTranslationResult(String resultText) {
        this.resultText = resultText;
    }
}
