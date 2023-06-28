package com.hyundaiautoever.HEAT.v1.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hyundaiautoever.HEAT.v1.dto.RequestTranslationDto;
import com.hyundaiautoever.HEAT.v1.dto.TranslationDto;
import com.hyundaiautoever.HEAT.v1.entity.Language;
import com.hyundaiautoever.HEAT.v1.entity.Translation;
import com.hyundaiautoever.HEAT.v1.entity.User;
import com.hyundaiautoever.HEAT.v1.repository.LanguageRepository;
import com.hyundaiautoever.HEAT.v1.repository.TranslationRepository;
import com.hyundaiautoever.HEAT.v1.repository.UserRepository;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.Request;
import org.apache.tika.langdetect.OptimaizeLangDetector;
import org.apache.tika.language.detect.LanguageDetector;
import org.apache.tika.language.detect.LanguageResult;
import org.aspectj.apache.bcel.classfile.Module.Open;
import org.hibernate.dialect.lock.OptimisticEntityLockException;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
@Slf4j
public class TranslationService {

    @Value("${env.open-ai.key}")
    private String OPEN_AI_KEY;
    @Value("${env.open-ai.uri}")
    private String OPEN_AI_URI;
    private String REQUEST_MESSAGE = "Translate this text into";
    private final LanguageRepository languageRepository;
    private final TranslationRepository translationRepository;
    private final UserRepository userRepository;


    public TranslationDto getTranslationDto(RequestTranslationDto requestTranslationDto) throws IOException{

        OpenAiResponse openAiResponse;
        Translation saveResultTranslation;

        //1. openai api 요청 보내기
        //2. DB 저장 위한 정보 구성 및 저장 (추가 작업 : 요청 언어 확인)
        //3. DB로부터 정보 가져와서 Response DTO 발송
        try {
            openAiResponse = getOpenAiResponse(requestTranslationDto);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        saveResultTranslation = saveTranslationRequest(requestTranslationDto, openAiResponse);
        TranslationDto translationDto = new TranslationDto(saveResultTranslation);
        return translationDto;
    }

    public List<TranslationDto> getUserTranslationHistory(String userId){
        List<Translation> userTranslationHistory= translationRepository.findByUser(userRepository.findByUserId(userId));
        List<TranslationDto> userTranslationDtoHistory = new ArrayList<>();
        for (Translation translation : userTranslationHistory) {
            userTranslationDtoHistory.add(new TranslationDto(translation));
        }
        return userTranslationDtoHistory;
    }

    private Language detectLanguageType(RequestTranslationDto requestTranslationDto) throws IOException {
        LanguageDetector detector = new OptimaizeLangDetector().loadModels();
        LanguageResult result = detector.detect(requestTranslationDto.getRequestText());
        return languageRepository.findByLanguageCode(result.getLanguage());
    }

    private Translation saveTranslationRequest(RequestTranslationDto requestTranslationDto,
        OpenAiResponse openAiResponse) throws IOException {
        Translation saveRequestTranslation = new Translation();

        saveRequestTranslation.setUser(userRepository.findByUserAccountNo(requestTranslationDto.getUserAccountNo()));
        saveRequestTranslation.setRequestLanguageNo(detectLanguageType(requestTranslationDto));
        saveRequestTranslation.setResultLanguageNo(languageRepository.findByLanguageNo(requestTranslationDto.getRequestLanguageNo()));
        saveRequestTranslation.setCreateDatetime(new Timestamp(System.currentTimeMillis()));
        saveRequestTranslation.setRequestText(requestTranslationDto.getRequestText());
        saveRequestTranslation.setResultText(openAiResponse.getChoices().get(0).getMessage().getContent());

         return translationRepository.save(saveRequestTranslation);
    }

    private OpenAiResponse getOpenAiResponse(RequestTranslationDto requestTranslationDto)
        throws JsonProcessingException {
        OpenAiResponse openAiResponse;
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
        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<String> responseHttpEntity = restTemplate.postForEntity(OPEN_AI_URI,
            openAiRequest, String.class);

        //openAiResponse 객체로 변환
        try {
            openAiResponse = objectMapper.readValue(responseHttpEntity.getBody(),
                OpenAiResponse.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return openAiResponse;
    }

    private String getRequestContent(RequestTranslationDto requestTranslationDto) {
        String requestContent;
        String requestText = requestTranslationDto.getRequestText();
        String requestLanguageName = languageRepository
            .findByLanguageNo(requestTranslationDto.getRequestLanguageNo())
            .getLanguageName();

        requestContent = REQUEST_MESSAGE + requestLanguageName + ":" + requestText;
        return requestContent;
    }
}
