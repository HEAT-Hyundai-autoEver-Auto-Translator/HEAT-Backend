package com.hyundaiautoever.HEAT.v1.exception;

public class UserAlreadyExistException extends Exception{

    public UserAlreadyExistException(String message) {
        super(message);
    }
}

// 전역적으로 처리할 수 있는