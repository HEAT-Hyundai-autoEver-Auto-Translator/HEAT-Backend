package com.hyundaiautoever.HEAT.v1.service;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hyundaiautoever.HEAT.v1.dto.OpenAIResponseDto;
import com.hyundaiautoever.HEAT.v1.dto.RequestTranslationDto;
import com.hyundaiautoever.HEAT.v1.entity.Translation;
import com.hyundaiautoever.HEAT.v1.repository.LanguageRepository;
import com.hyundaiautoever.HEAT.v1.repository.TranslationRepository;
import java.io.IOException;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class OpenAIService {

    @Value("${env.open-ai.key}")
    private static String OPEN_AI_KEY;
    @Value("${env.open-ai.uri}")
    private static String OPEN_AI_URI;
    private static final String REQUEST_MESSAGE = "Translate this text into";
    private final LanguageRepository languageRepository;
    private final TranslationRepository translationRepository;

    @Builder
    public static class OpenAIRequestForm {

        private String source;
        private String target;
        private String text;

    }

    /**
     * OpenAI API에 번역 요청을 전송한 뒤 resultText를 DB의 해당 레코드에 저장한다.
     *
     * @param emptyResultTranslation resultText가 포함돼 있지 않은 Translation 엔티티
     * @param requestTranslationDto  클라이언트의 번역 요청 정보
     * @throws JsonProcessingException OpenAI API 결과 ObjectMapping 실패 시
     */

    @Async
    public void getOpenAIResponseAndSave(Translation emptyResultTranslation,
        RequestTranslationDto requestTranslationDto) throws JsonProcessingException {
        OpenAIResponseDto openAiResponseDto;
        ObjectMapper objectMapper = new ObjectMapper();

        //set header
        HttpHeaders httpHeaders = new org.springframework.http.HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.setBearerAuth(OPEN_AI_KEY);

        //set body
        ObjectNode body = objectMapper.createObjectNode();
        body.put("model", "gpt-3.5-turbo");
        ArrayNode messages = objectMapper.createArrayNode();
        ObjectNode messagesObject = objectMapper.createObjectNode();
        messagesObject.put("role", "user");
        messagesObject.put("content", getRequestContent(requestTranslationDto));
        messages.add(messagesObject);
        body.set("messages", messages);

        //Create HTTP Entity
        HttpEntity<?> openAiRequest = new HttpEntity<>(body.toString(), httpHeaders);

        //Send HTTP POST request
        RestTemplate restTemplate = new RestTemplate(); //webclient
        HttpEntity<String> responseHttpEntity = restTemplate.postForEntity(OPEN_AI_URI,
            openAiRequest, String.class);

        //openAiResponse 객체로 변환
        try {
            openAiResponseDto = objectMapper.readValue(responseHttpEntity.getBody(),
                OpenAIResponseDto.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        // openAi 결과 수령후 DB에 저장
        saveCompleteResultTranslation(emptyResultTranslation, openAiResponseDto);
    }

    /**
     * resultText를 DB의 해당 레코드에 저장한다
     *
     * @param emptyResultTranslation resultText가 포함돼 있지 않은 Translation 엔티티
     * @param openAIResponseDto      openAI response JSON을 객체화 한 클래스
     * @throws Exception 처리예정
     */

    @Transactional
    public Translation saveCompleteResultTranslation(Translation emptyResultTranslation,
        OpenAIResponseDto openAIResponseDto) {
        Translation fullRequestTranslation = translationRepository.findByTranslationNo(
            emptyResultTranslation.getTranslationNo());
        fullRequestTranslation.setResultText(
            openAIResponseDto.getChoices().get(0).getMessage().getContent());
        return translationRepository.save(fullRequestTranslation);
    }

    /**
     * OpenAI API 요청 텍스트를 생성한다.
     *
     * @param requestTranslationDto 번역 요청 정보
     * @throws Exception 처리예정
     */

    @Transactional
    public String getRequestContent(RequestTranslationDto requestTranslationDto) {
        String requestContent;
        String requestText = requestTranslationDto.getRequestText();
        String requestLanguageName;
        requestLanguageName = languageRepository.findByLanguageNo(
            requestTranslationDto.getResultLanguageNo()).getLanguageName();
        requestContent = REQUEST_MESSAGE + requestLanguageName + ":" + requestText;
        return requestContent;
    }
}
