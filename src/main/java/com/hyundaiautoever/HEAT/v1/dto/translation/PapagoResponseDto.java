package com.hyundaiautoever.HEAT.v1.dto.translation;

import lombok.Builder;
import lombok.Getter;

//@Builder
public class PapagoResponseDto {

    @Getter
    private Message message;

    @Getter
//    @Builder
    public static class Message {

        private Result result;
    }

    @Getter
//    @Builder
    public static class Result {

        private String srcLangType;
        private String tarLangType;
        private String translatedText;
    }

}
