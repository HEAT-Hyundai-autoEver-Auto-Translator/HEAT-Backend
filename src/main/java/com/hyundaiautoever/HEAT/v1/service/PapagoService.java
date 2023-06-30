package com.hyundaiautoever.HEAT.v1.service;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hyundaiautoever.HEAT.v1.dto.OpenAIResponseDto;
import com.hyundaiautoever.HEAT.v1.dto.PapagoResponseDto;
import com.hyundaiautoever.HEAT.v1.dto.RequestTranslationDto;
import com.hyundaiautoever.HEAT.v1.entity.Translation;
import com.hyundaiautoever.HEAT.v1.repository.LanguageRepository;
import com.hyundaiautoever.HEAT.v1.repository.TranslationRepository;
import java.io.IOException;
import java.io.Serializable;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.language.translate.EmptyTranslator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.web.reactive.function.client.WebClientCodecCustomizer;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@RequiredArgsConstructor
@Slf4j
public class PapagoService {

    @Value("${env.papago.id}")
    private String PAPAGO_ID;
    @Value("${env.papago.secret}")
    private String PAPAGO_SECRET;
    @Value("${env.papago.uri}")
    private String PAPAGO_URI;
    private static String DB_CHINESE_CODE = "zh";
    private static String PAPAGO_CHINESE_CODE = "zh-CN";

    private final LanguageRepository languageRepository;
    private final TranslationRepository translationRepository;
    private final LanguageService languageService;


    @Builder
    @Getter
    public static class PapagoRequestForm implements Serializable{

        private String source;
        private String target;
        private String text;
    }

    /**
     * Papago API에 번역 요청을 전송한 뒤 resultText를 DB의 해당 레코드에 저장한다.
     *
     * @param emptyResultTranslation resultText가 포함돼 있지 않은 Translation 엔티티
     * @param requestTranslationDto  클라이언트의 번역 요청 정보
     * @throws JsonProcessingException OpenAI API 결과 ObjectMapping 실패 시
     */

    @Async
    public void getPapagoResponseAndSave(Translation emptyResultTranslation,
        RequestTranslationDto requestTranslationDto) {

        ObjectMapper objectMapper = new ObjectMapper();

        // 결과 언어가 중국어의 경우 타겟 언어 코드 변환 (zh -> zh-CN : 파파고 중국어 간체자 코드)
        String targetLanguageCode = languageRepository.findByLanguageNo(
            requestTranslationDto.getResultLanguageNo()).getLanguageCode();
        if (targetLanguageCode.equals(DB_CHINESE_CODE)) {
            targetLanguageCode = PAPAGO_CHINESE_CODE;
            log.info("after : " + targetLanguageCode);
        }

        //Papago 요청을 위한 객체 생성
        PapagoRequestForm papagoRequestForm = PapagoRequestForm.builder()
            .source(languageService.detectLanguageType(requestTranslationDto).getLanguageCode())
            .target(targetLanguageCode)
            .text(requestTranslationDto.getRequestText())
            .build();

        //객체를 Json으로 변환
        String papagoRequestJson;
        try {
            papagoRequestJson = objectMapper.writeValueAsString(papagoRequestForm);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        //Http 요청 생성 및 응답 수신
        WebClient webClient = WebClient.create(PAPAGO_URI);

        PapagoResponseDto papagoResponseDto = webClient.post()
            .header("X-NCP-APIGW-API-KEY-ID", PAPAGO_ID)
            .header("X-NCP-APIGW-API-KEY", PAPAGO_SECRET)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(papagoRequestJson)
            .retrieve()
            .bodyToMono(PapagoResponseDto.class)
            .block();

        //결과 수신 후 DB에 저장
        saveCompleteResultTranslation(emptyResultTranslation, papagoResponseDto);
    }

    /**
     * resultText를 DB의 해당 레코드에 저장한다
     *
     * @param emptyResultTranslation resultText가 포함돼 있지 않은 Translation 엔티티
     * @param papagoResponseDto      papago response JSON을 객체화 한 클래스
     * @throws Exception 처리예정
     */

    @Transactional
    public Translation saveCompleteResultTranslation(Translation emptyResultTranslation,
        PapagoResponseDto papagoResponseDto) {

        //Translation 레코드 찾기
        Translation fullRequestTranslation = translationRepository.findByTranslationNo(
            emptyResultTranslation.getTranslationNo());

        //Translation 레코드에 결과 텍스트 저장하기
        fullRequestTranslation.setResultText(
            papagoResponseDto.getMessage().getResult().getTranslatedText());

        return translationRepository.save(fullRequestTranslation);
    }
}
