package com.hyundaiautoever.HEAT.v1.entitiy;

import java.util.Date;
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
@Getter @Setter
@Table(name = "user", indexes = @Index(name = "idx_user_id", columnList = "user_id"))
public class User {

    @Id @GeneratedValue
    @Column(name = "user_account_no")
    private Long id;

    @Column(name = "user_id")
    private String userId;
    private String passwordHash;
    private String userName;
    private String userRole;
    private String profileImageUrl;
    private String refreshToken;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "language_no")
    private Language language;

    private Date signupDate;
    private Date lastAccessDate;
}
