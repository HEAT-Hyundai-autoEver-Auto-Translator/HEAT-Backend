package com.hyundaiautoever.HEAT.v1.Exception;

public class TranslationNotCompleteException extends RuntimeException{
    public TranslationNotCompleteException(String message) {
        super(message);
    }
}