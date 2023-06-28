package com.hyundaiautoever.HEAT.v1.config;

import com.hyundaiautoever.HEAT.v1.exception.TranslationNotCompleteException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(TranslationNotCompleteException.class)
    public ResponseEntity<String> handleTranslationNotFoundException(TranslationNotCompleteException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }
}
