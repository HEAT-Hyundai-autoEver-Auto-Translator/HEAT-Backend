package com.hyundaiautoever.HEAT.v1.exception;

import javax.validation.constraints.Null;

public class LanguageDetectionFailureException extends NullPointerException {

    public LanguageDetectionFailureException(String message) {
        super(message);
    }
}
