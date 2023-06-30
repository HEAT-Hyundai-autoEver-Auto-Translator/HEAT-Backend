package com.hyundaiautoever.HEAT.v1.service;

import com.hyundaiautoever.HEAT.v1.dto.RequestTranslationDto;
import com.hyundaiautoever.HEAT.v1.entity.Language;
import com.hyundaiautoever.HEAT.v1.repository.LanguageRepository;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.apache.tika.langdetect.OptimaizeLangDetector;
import org.apache.tika.language.detect.LanguageDetector;
import org.apache.tika.language.detect.LanguageResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LanguageService {

    private final LanguageRepository languageRepository;

    /**
     * 텍스트를 기반으로 번역 요청 언어를 감지한다.
     *
     * @param requestTranslationDto 번역 테이블 기본키 값
     * @throws Exception 처리예정
     */

    @Transactional
    public Language detectLanguageType(RequestTranslationDto requestTranslationDto) {
        LanguageDetector detector = new OptimaizeLangDetector().loadModels();
        LanguageResult result = detector.detect(requestTranslationDto.getRequestText());
        return languageRepository.findByLanguageCode(result.getLanguage());
    }

}
