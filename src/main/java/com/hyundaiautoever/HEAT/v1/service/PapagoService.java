package com.hyundaiautoever.HEAT.v1.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hyundaiautoever.HEAT.v1.dto.translation.PapagoResponseDto;
import com.hyundaiautoever.HEAT.v1.dto.translation.RequestTranslationDto;
import com.hyundaiautoever.HEAT.v1.entity.Language;
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
    private final LanguageRepository languageRepository;
    private final TranslationRepository translationRepository;
    private final LanguageService languageService;

    @Async
    public void getPapagoResponseAndSave(Translation translationWithoutResult,
                                         RequestTranslationDto requestTranslationDto) {
        //OpenAPI 요청 전송 및 요청 결과 수신
        PapagoResponseDto papagoResponseDto = getPapagoResponseDto(requestTranslationDto);
        //번역 내용 DB에 저장
        saveCompleteResultTranslation(translationWithoutResult, papagoResponseDto);
    }

    private PapagoResponseDto getPapagoResponseDto(RequestTranslationDto requestTranslationDto) {
        ObjectMapper objectMapper = new ObjectMapper();

        String requestLanguageCode = languageService.detectLanguageType(requestTranslationDto).getLanguageCode();
        String resultLanguageCode = languageRepository.findByLanguageName(requestTranslationDto.getResultLanguageName()).getLanguageCode();

        // Http 요청 바디 생성
        ObjectNode papagoRequestBody = objectMapper.createObjectNode();
        papagoRequestBody.put("source", requestLanguageCode);
        papagoRequestBody.put("target", resultLanguageCode);
        papagoRequestBody.put("text", requestTranslationDto.getRequestText());

        //Http 요청 생성 및 응답 수신
        WebClient webClient = WebClient.create(PAPAGO_URI);
        PapagoResponseDto papagoResponseDto = webClient.post()
                .header("X-NCP-APIGW-API-KEY-ID", PAPAGO_ID)
                .header("X-NCP-APIGW-API-KEY", PAPAGO_SECRET)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(papagoRequestBody)
                .retrieve()
                .bodyToMono(PapagoResponseDto.class)
                .block();
        return papagoResponseDto;
    }

    @Transactional
    public Translation saveCompleteResultTranslation(Translation translationWithoutResult,
                                                     PapagoResponseDto papagoResponseDto) {

        //Translation 레코드 찾기
        Translation fullRequestTranslation = translationRepository.findByTranslationNo(
                translationWithoutResult.getTranslationNo());

        //Translation 레코드에 결과 텍스트 저장하기
        fullRequestTranslation.setResultText(
                papagoResponseDto.getMessage().getResult().getTranslatedText());

        return translationRepository.save(fullRequestTranslation);
    }


//    public Language papagoLanguageDetection(RequestTranslationDto requestTranslationDto) {
//
//
//    }
}
