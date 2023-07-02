package com.hyundaiautoever.HEAT.v1.dto;

import lombok.Getter;

public class PapagoResponseDto {

    @Getter
    private Message message;

    @Getter
    public static class Message {

        private Result result;
    }

    @Getter
    public static class Result {

        private String srcLangType;
        private String tarLangType;
        private String translatedText;
    }

}
