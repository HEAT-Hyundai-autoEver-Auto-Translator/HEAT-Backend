package com.hyundaiautoever.HEAT.v1.entitiy;

import java.sql.Timestamp;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "translation", indexes = @Index(name = "idx_user_account_no", columnList = "user_account_no"))
public class Translation {

    @Id
    @GeneratedValue
    private Long translationNo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_account_no")
    private User userAccountNo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "request_language_no")
    private Language requestLanguageNo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "result_language_no")
    private Language resultLanguageNo;

    private Timestamp createDatetime;
    private String requestText;
    private String resultText;
}
