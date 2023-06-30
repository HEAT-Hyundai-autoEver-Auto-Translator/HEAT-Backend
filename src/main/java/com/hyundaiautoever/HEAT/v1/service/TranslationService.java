package com.hyundaiautoever.HEAT.v1.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.hyundaiautoever.HEAT.v1.dto.RequestTranslationDto;
import com.hyundaiautoever.HEAT.v1.dto.TranslationDto;
import com.hyundaiautoever.HEAT.v1.entity.Translation;
import com.hyundaiautoever.HEAT.v1.exception.TranslationNotCompleteException;
import com.hyundaiautoever.HEAT.v1.exception.TranslationNotFoundException;
import com.hyundaiautoever.HEAT.v1.repository.LanguageRepository;
import com.hyundaiautoever.HEAT.v1.repository.TranslationRepository;
import com.hyundaiautoever.HEAT.v1.repository.UserRepository;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    private final PapagoService papagoService;
    private final LanguageService languageService;

    /**
     * 번역요청을 처리한다.
     *
     * @param requestTranslationDto 클라이언트의 번역 요청 정보
     * @throws Exception 처리예정
     */

    public Long requestTranslation(RequestTranslationDto requestTranslationDto) {

        Translation emptyResultTranslation;

        //DB에 ResultText 칼럼 제외하고 저장
        emptyResultTranslation = saveEmptyRequstTranslation(requestTranslationDto);

        //openAI API 요청 비동기로 진행 및 결과 저장
//        try {
        papagoService.getPapagoResponseAndSave(emptyResultTranslation, requestTranslationDto);
//            openAIService.getOpenAIResponseAndSave(emptyResultTranslation, requestTranslationDto);
//        } catch (JsonProcessingException e) {
//            throw new RuntimeException(e);
//        }

        //저장된 translationNo 반환
        return emptyResultTranslation.getTranslationNo();
    }

    /**
     * 번역요청 결과를 반환한다.
     *
     * @param translationNo 번역 테이블 기본키 값
     * @throws TranslationNotCompleteException 해당 레코드에 아직 결과값이 저장돼 있지 않았을 떄
     */

    @Transactional   // 필요 여부를 판단하기 Transactional을 전역적으로 처리하는 법
    public Optional<TranslationDto> getTranslationResult(Long translationNo) {
        Translation translation = translationRepository.findByTranslationNo(translationNo);
        if (translation == null) {
            throw new TranslationNotFoundException("잘못된 번역 데이터 요청입니다.");
        }
        if (translation.getResultText() == null) {
            throw new TranslationNotCompleteException("번역 작업이 완료되지 않았습니다.");
        }
        Optional<TranslationDto> translationDto = Optional.ofNullable(
            new TranslationDto(translation));
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
    public Translation saveEmptyRequstTranslation(RequestTranslationDto requestTranslationDto) {
        Translation emptyRequstTranslation = new Translation();
        //User 세팅
        emptyRequstTranslation.setUser(
            userRepository.findByUserAccountNo(requestTranslationDto.getUserAccountNo()));

        //RequestLanguageNo 세팅
        emptyRequstTranslation.setRequestLanguageNo(
            languageService.detectLanguageType(requestTranslationDto));

        //ResultLanguage No 세팅
        emptyRequstTranslation.setResultLanguageNo(
            languageRepository.findByLanguageNo(requestTranslationDto.getResultLanguageNo()));

        //CreateDatetime 세팅
        emptyRequstTranslation.setCreateDatetime(new Timestamp(System.currentTimeMillis()));

        //RequestText 세팅
        emptyRequstTranslation.setRequestText(requestTranslationDto.getRequestText());

        return translationRepository.save(emptyRequstTranslation);
    }

}
