package com.hyundaiautoever.HEAT.v1.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Entity
@Getter
@Setter
public class Language {

    @Id
    @GeneratedValue
    private Integer languageNo;
    @Column(nullable = false)
    private String languageName;
    @Column(nullable = false)
    private String languageCode;
}
