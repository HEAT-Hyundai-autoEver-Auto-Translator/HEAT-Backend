package com.hyundaiautoever.HEAT.v1.service;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hyundaiautoever.HEAT.v1.dto.translation.OpenAIResponseDto;
import com.hyundaiautoever.HEAT.v1.dto.translation.RequestTranslationDto;
import com.hyundaiautoever.HEAT.v1.entity.Translation;
import com.hyundaiautoever.HEAT.v1.repository.LanguageRepository;
import com.hyundaiautoever.HEAT.v1.repository.translation.TranslationRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.reactive.function.client.WebClient;

import javax.persistence.EntityNotFoundException;
import javax.security.sasl.AuthenticationException;
import java.io.IOException;

@Service
@RequiredArgsConstructor
@Slf4j
public class OpenAIService {

    @Value("${env.open-ai.key}")
    private String OPEN_AI_KEY;
    @Value("${env.open-ai.uri}")
    private String OPEN_AI_URI;
    private static final String OPEN_AI_MODEL = "gpt-3.5-turbo";
    private static final String OPEN_AI_ROLE = "user";
    private static final String REQUEST_MESSAGE = "Translate this text into ";
    private final TranslationRepository translationRepository;

    /**
     * 번역 요청 정보를 기반으로 OpenAI에 번역 결과를 비동기 방식으로 요청하고 결과를 DB에 저장한다.
     *
     * @param translationWithoutResult
     **/
    @Async
    public void getOpenAIResponseAndSave(Translation translationWithoutResult,
                                         RequestTranslationDto requestTranslationDto) {
        // openAI API 번역 요청 및 결과 반환
        OpenAIResponseDto openAiResponseDto = getOpenAIResponseDto(requestTranslationDto);
        // openAi 결과 수신 후 DB에 저장
        saveCompleteResultTranslation(translationWithoutResult, openAiResponseDto);
    }

    /**
     * 번역 요청 정보를 기반으로 OpenAI에 해당 요청을 전송한다.
     *
     * @param requestTranslationDto
     * @return openAIResponseDto OpenAI의 번역 결과를 Dto 객체로 변환한 결과 값
     **/
    private OpenAIResponseDto getOpenAIResponseDto(RequestTranslationDto requestTranslationDto) {
        ObjectMapper objectMapper = new ObjectMapper();

        //OpenAI 요청 바디 생성
        ObjectNode openAIRequestBody = objectMapper.createObjectNode();
        openAIRequestBody.put("model", OPEN_AI_MODEL);
        ArrayNode messages = objectMapper.createArrayNode();
        ObjectNode messagesObject = objectMapper.createObjectNode();
        messagesObject.put("role", OPEN_AI_ROLE);
        messagesObject.put("content", getRequestContent(requestTranslationDto));
        messages.add(messagesObject);
        openAIRequestBody.put("messages", messages);

        //Http 요청 생성 및 응답 수신
        WebClient webClient = WebClient.create(OPEN_AI_URI);
        OpenAIResponseDto openAiResponseDto = webClient.post()
                .header("Authorization", "Bearer " + OPEN_AI_KEY)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(openAIRequestBody)
                .retrieve()
                .bodyToMono(OpenAIResponseDto.class)
                .block();

        if (openAiResponseDto == null || openAiResponseDto.getChoices().get(0).getMessage().getContent() == null) {
            throw new IllegalStateException("OpenAI API 호출 실패");
        }

        return openAiResponseDto;
    }

    /**
     * 번역 결과를 DB에 저장한다.
     *
     * @param translationWithoutResult,openAiResponseDto
     * @return 저장된 translation 객체
     * @throws EntityNotFoundException translationWithoutResult 가 잘못된 정보일 경우
     **/
    @Transactional
    public Translation saveCompleteResultTranslation(Translation translationWithoutResult,
                                                     OpenAIResponseDto openAIResponseDto) {
        // Translation 인덱스로
        Translation fullRequestTranslation = translationRepository.findByTranslationNo(
                        translationWithoutResult.getTranslationNo())
                .orElseThrow(() -> new EntityNotFoundException("존재 하지 않는 번역 정보입니다."));
        fullRequestTranslation.setTranslationResult(
                openAIResponseDto.getChoices().get(0).getMessage().getContent());
        return translationRepository.save(fullRequestTranslation);
    }

    /**
     * OpenAI에 요청할 content 양식을 지정한다.
     * form = REQUEST_MESSAGE + 격과 언어 : 요청 텍스트
     *
     * @param requestTranslationDto
     * @return API 요청을 위한 content
     **/
    private String getRequestContent(RequestTranslationDto requestTranslationDto) {
        String requestContent;
        String requestText = requestTranslationDto.getRequestText();
        String requestLanguageName;
        requestLanguageName = requestTranslationDto.getResultLanguageName();
        requestContent = REQUEST_MESSAGE + requestLanguageName + " : " + requestText;
        return requestContent;
    }
}
