package com.hyundaiautoever.HEAT.v1.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
public class Language {

    @Id
    @GeneratedValue
    private Integer languageNo;
    private String languageName;
    private String languageCode;
}
