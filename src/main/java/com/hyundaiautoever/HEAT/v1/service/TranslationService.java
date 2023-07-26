package com.hyundaiautoever.HEAT.v1.service;

import com.hyundaiautoever.HEAT.v1.entity.Language;
import com.hyundaiautoever.HEAT.v1.exception.TranslationNotCompleteException;
import com.hyundaiautoever.HEAT.v1.dto.translation.RequestTranslationDto;
import com.hyundaiautoever.HEAT.v1.dto.translation.TranslationDto;
import com.hyundaiautoever.HEAT.v1.entity.Translation;
import com.hyundaiautoever.HEAT.v1.repository.LanguageRepository;
import com.hyundaiautoever.HEAT.v1.repository.translation.TranslationRepository;
import com.hyundaiautoever.HEAT.v1.repository.user.UserRepository;

import java.sql.Timestamp;
import java.util.List;

import com.hyundaiautoever.HEAT.v1.util.TranslationMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.Nullable;
import javax.persistence.EntityNotFoundException;

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

        // 요청언어와 결과언어가 같을 경우 요청 텍스트를 그대로 DB에 저장 및 별도의 API 요청 미발송
        if (StringUtils.hasText(translationWithoutResult.getResultText())) {
            return translationWithoutResult.getTranslationNo();
        }

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
     * @throws NullPointerException 해당 레코드에 결과값이 저장돼 있지 않을 떄
     */
    @Transactional(readOnly = true)
    public TranslationDto getTranslationResult(Long translationNo) {

        Translation translation = translationRepository.findByTranslationNo(translationNo)
                .orElseThrow(() -> new EntityNotFoundException("존재 하지 않는 번역 데이터 요청입니다."));
        if (translation.getResultText() == null) {
            throw new TranslationNotCompleteException("번역 작업이 완료되지 않았습니다.");
        }
        TranslationDto translationDto = new TranslationDto(translation);
        return translationDto;
    }

    /**
     * 유저 별 번역 이력을 반환한다.
     *
     * @param userEmail 유저의 email
     * @return 유저의 번역 이력 리스트
     */
    @Nullable
    @Transactional(readOnly = true)
    public List<TranslationDto> findTranslationByUserEmail(String userEmail) {
        return translationMapper.toTranslationDtoList(translationRepository.findTranslationByUserEmail(userEmail));
    }

    /**
     * DB 내 모든 번역 이력을 반환하다.
     *
     * @return 기존까지의 모든 번역 이력 리스트
     **/
    @Nullable
    @Transactional(readOnly = true)
    public List<TranslationDto> findAllTranslation() {
        return translationMapper.toTranslationDtoList(translationRepository.findAll());
    }

    /**
     * 특정 인덱스의 번역 이력을 삭제한다.
     **/
    @Transactional
    public void deleteTranslation(Long translationNo) {
        translationRepository.deleteById(translationNo);
    }

    /**
     * 번역 요청 정보를 결과 없이 DB에 저장한다.
     *
     * @return 해당 정보가 저장된 Translation 객체
     * @throws EntityNotFoundException 잘못된 유저 정보가 포함됐을 경우 예외 처리
     **/
    @Transactional
    public Translation saveTranslationWithoutResult(RequestTranslationDto requestTranslationDto) {

        if (!StringUtils.hasText(requestTranslationDto.getRequestText())) {
            throw new TranslationNotCompleteException("요청 텍스트가 비어있습니다.");
        }
        Language requestLanguage = languageService.detectLanguageType(requestTranslationDto);
        Language resultLanguage = languageRepository.findByLanguageName(requestTranslationDto.getResultLanguageName());

        Timestamp createDateTime = new Timestamp(System.currentTimeMillis());
        Translation emptyRequstTranslation = Translation.builder()
                .user(userRepository.findByUserAccountNo(requestTranslationDto.getUserAccountNo())
                        .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 유저 정보입니다.")))
                .requestLanguage(requestLanguage)
                .resultLanguage(resultLanguage)
                .createDatetime(createDateTime)
                .requestText(requestTranslationDto.getRequestText())
                .build();
        if (resultLanguage == resultLanguage) {
            emptyRequstTranslation.setTranslationResult(requestTranslationDto.getRequestText());
        }
        return translationRepository.save(emptyRequstTranslation);
    }
}