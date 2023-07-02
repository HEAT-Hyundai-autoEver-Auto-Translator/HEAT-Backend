package com.hyundaiautoever.HEAT.v1.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Getter;

@JsonIgnoreProperties(ignoreUnknown = true)
public class OpenAIResponseDto {

    @Getter
    private List<Choice> choices;

    @Getter
    public static class Choice {

        @JsonProperty("finish_reason")
        private String finishReason;
        private int index;
        private Message message;
    }

    @Getter
    public static class Message {

        private String content;
        private String role;
    }
}
