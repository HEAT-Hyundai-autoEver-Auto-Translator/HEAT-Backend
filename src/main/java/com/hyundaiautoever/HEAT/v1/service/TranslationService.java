package com.hyundaiautoever.HEAT.v1.service;

import com.hyundaiautoever.HEAT.v1.dto.translation.RequestTranslationDto;
import com.hyundaiautoever.HEAT.v1.dto.translation.TranslationDto;
import com.hyundaiautoever.HEAT.v1.entity.Translation;
import com.hyundaiautoever.HEAT.v1.exception.TranslationNotCompleteException;
import com.hyundaiautoever.HEAT.v1.exception.TranslationNotFoundException;
import com.hyundaiautoever.HEAT.v1.repository.LanguageRepository;
import com.hyundaiautoever.HEAT.v1.repository.translation.TranslationRepository;
import com.hyundaiautoever.HEAT.v1.repository.user.UserRepository;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

import com.hyundaiautoever.HEAT.v1.util.TranslationMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Nullable;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class TranslationService {

    private final LanguageRepository languageRepository;
    private final TranslationRepository translationRepository;
    private final UserRepository userRepository;
    private final OpenAIService openAIService;
    private final PapagoService papagoService;
    private final LanguageService languageService;
    private final TranslationMapper translationMapper = Mappers.getMapper(TranslationMapper.class);

    /**
     * 번역요청을 처리한다.
     *
     * @param requestTranslationDto 클라이언트의 번역 요청 정보
     * @return 요청한 번역 레코드의 기본 키 값(translationNo)
     * @throws Exception 처리예정
     */
    public Long requestTranslation(RequestTranslationDto requestTranslationDto) {

        // 번역 요청 정보 ResultText 칼럼 제외하고 DB에 저장
        Translation translationWithoutResult = saveTranslationWithoutResult(requestTranslationDto);

        //요청 언어 결과 언어 요청 페어에 따라 파파고, OpenAI 분기 처리
        if (languageService.isPapagoSupported(translationWithoutResult)) {
            papagoService.getPapagoResponseAndSave(translationWithoutResult, requestTranslationDto);
        } else {
            openAIService.getOpenAIResponseAndSave(translationWithoutResult, requestTranslationDto);
        }

        //저장된 translationNo 반환
        return translationWithoutResult.getTranslationNo();
    }


    /**
     * 번역요청 결과를 반환한다.
     *
     * @param translationNo 번역 테이블 기본키 값
     * @return 해당 번역 요청의 결과를 포함한 TranslationDto
     * @throws TranslationNotCompleteException 해당 레코드에 결과값이 저장돼 있지 않을 떄
     */
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
     * @param userId 유저의 id
     * @return 유저의 번역 이력 리스트
     */
    @Nullable
    public List<TranslationDto> findTranslationByUserEmail(String userId) {
        return translationMapper.toTranslationDtoList(translationRepository.findTranslationByUserEmail(userId));
    }


    /**
     * DB 내 모든 번역 이력을 반환하다.
     *
     * @return 기존까지의 모든 번역 이력 리스트
     **/
    @Nullable
    public List<TranslationDto> findAllTranslation() {
        return translationMapper.toTranslationDtoList(translationRepository.findAll());
    }

    public void deleteTranslation(Long translationNo) {
        translationRepository.deleteById(translationNo);
    }

    public Translation saveTranslationWithoutResult(RequestTranslationDto requestTranslationDto) {

        Translation emptyRequstTranslation = new Translation();
        //User 세팅
        emptyRequstTranslation.setUser(
                userRepository.findByUserAccountNo(requestTranslationDto.getUserAccountNo()));
        //requestLanguage 세팅
        emptyRequstTranslation.setRequestLanguage(
                languageService.detectLanguageType(requestTranslationDto));
        //ResultLanguage No 세팅
        emptyRequstTranslation.setResultLanguage(
                languageRepository.findByLanguageName(requestTranslationDto.getResultLanguageName()));
        //CreateDatetime 세팅
        emptyRequstTranslation.setCreateDatetime(new Timestamp(System.currentTimeMillis()));
        //RequestText 세팅
        emptyRequstTranslation.setRequestText(requestTranslationDto.getRequestText());

        return translationRepository.save(emptyRequstTranslation);
    }
}