package com.hyundaiautoever.HEAT.v1.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.hyundaiautoever.HEAT.v1.dto.RequestTranslationDto;
import com.hyundaiautoever.HEAT.v1.dto.TranslationDto;
import com.hyundaiautoever.HEAT.v1.entity.Language;
import com.hyundaiautoever.HEAT.v1.entity.Translation;
import com.hyundaiautoever.HEAT.v1.exception.TranslationNotCompleteException;
import com.hyundaiautoever.HEAT.v1.repository.LanguageRepository;
import com.hyundaiautoever.HEAT.v1.repository.TranslationRepository;
import com.hyundaiautoever.HEAT.v1.repository.UserRepository;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.langdetect.OptimaizeLangDetector;
import org.apache.tika.language.detect.LanguageDetector;
import org.apache.tika.language.detect.LanguageResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class TranslationService {

    private final LanguageRepository languageRepository;
    private final TranslationRepository translationRepository;
    private final UserRepository userRepository;
    private final OpenAIService openAIService;

    /**
     * 번역요청을 처리한다.
     *
     * @param requestTranslationDto 클라이언트의 번역 요청 정보
     * @throws Exception 처리예정
     */

    public Long requestTranslation(RequestTranslationDto requestTranslationDto) throws IOException {

        Translation emptyResultTranslation;

        //DB에 ResultText 칼럼 제외하고 저장
        emptyResultTranslation = saveEmptyRequstTranslation(requestTranslationDto);

        //openAI API 요청 비동기로 진행 및 결과 저장
        try {
            openAIService.getOpenAIResponseAndSave(emptyResultTranslation, requestTranslationDto);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        //저장된 translationNo 반환
        return emptyResultTranslation.getTranslationNo();
    }

    /**
     * 번역요청 결과를 반환한다.
     *
     * @param translationNo 번역 테이블 기본키 값
     * @throws TranslationNotCompleteException 해당 레코드에 아직 결과값이 저장돼 있지 않았을 떄
     */

    @Transactional
    public TranslationDto getTranslationResult(Long translationNo) {
        Translation translation = translationRepository.findByTranslationNo(translationNo);
        if (translation.getResultText() == null) {
            throw new TranslationNotCompleteException("번역 작업이 완료되지 않았습니다.");
        }
        TranslationDto translationDto = new TranslationDto(translation);
        return translationDto;
    }

    /**
     * 유저 별 번역 이력을 반환한다.
     *
     * @param userId 번역 테이블 기본키 값
     * @throws Exception 처리예정
     */

    @Transactional
    public List<TranslationDto> findTranslationByUserId(String userId) {
        List<Translation> userTranslationHistory = translationRepository.findByUser(
            userRepository.findByUserId(userId));
        List<TranslationDto> userTranslationDtoHistory = new ArrayList<>();
        for (Translation translation : userTranslationHistory) {
            userTranslationDtoHistory.add(new TranslationDto(translation));
        }
        return userTranslationDtoHistory;
    }

    /**
     * DB 내 모든 번역 이력을 반환하다.
     *
     * @throws Exception 처리예정
     */

    @Transactional
    public List<TranslationDto> findAllTranslation() {
        List<Translation> userTranslationHistory = translationRepository.findAll();
        List<TranslationDto> userTranslationDtoHistory = new ArrayList<>();
        for (Translation translation : userTranslationHistory) {
            userTranslationDtoHistory.add(new TranslationDto(translation));
        }
        return userTranslationDtoHistory;
    }

    /**
     * 번역 요청이 들어왔을 때, 결과 텍스트를 제외한 나머지 칼럼을 채운 채로 DB에 저장한다.
     *
     * @param requestTranslationDto 번역 테이블 기본키 값
     * @throws Exception 처리예정
     */

    @Transactional
    public Translation saveEmptyRequstTranslation(RequestTranslationDto requestTranslationDto)
        throws IOException {
        Translation emptyRequstTranslation = new Translation();
        emptyRequstTranslation.setUser(
            userRepository.findByUserAccountNo(requestTranslationDto.getUserAccountNo()));
        emptyRequstTranslation.setRequestLanguageNo(detectLanguageType(requestTranslationDto));
        emptyRequstTranslation.setResultLanguageNo(
            languageRepository.findByLanguageNo(requestTranslationDto.getRequestLanguageNo()));
        emptyRequstTranslation.setCreateDatetime(new Timestamp(System.currentTimeMillis()));
        emptyRequstTranslation.setRequestText(requestTranslationDto.getRequestText());
        return translationRepository.save(emptyRequstTranslation);
    }

    /**
     * 텍스트를 기반으로 번역 요청 언어를 감지한다.
     *
     * @param requestTranslationDto 번역 테이블 기본키 값
     * @throws Exception 처리예정
     */

    @Transactional
    public Language detectLanguageType(RequestTranslationDto requestTranslationDto)
        throws IOException {
        LanguageDetector detector = new OptimaizeLangDetector().loadModels();
        LanguageResult result = detector.detect(requestTranslationDto.getRequestText());
        return languageRepository.findByLanguageCode(result.getLanguage());
    }
}
