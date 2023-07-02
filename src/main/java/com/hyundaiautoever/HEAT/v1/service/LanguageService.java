package com.hyundaiautoever.HEAT.v1.service;

import com.hyundaiautoever.HEAT.v1.dto.LanguageDto;
import com.hyundaiautoever.HEAT.v1.dto.RequestTranslationDto;
import com.hyundaiautoever.HEAT.v1.entity.Language;
import com.hyundaiautoever.HEAT.v1.entity.Translation;
import com.hyundaiautoever.HEAT.v1.exception.LanguageDetectionFailureException;
import com.hyundaiautoever.HEAT.v1.repository.LanguageRepository;

import java.util.*;

import lombok.Getter;
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
    private static final Map<LanguageCodePair, Boolean> languageSupportMap = new HashMap<>();

    @Getter
    private static class LanguageCodePair {

        private final String requestLanguageCode;
        private final String resultLanguageCode;

        public LanguageCodePair(String requestLanguageCode, String resultLanguageCode) {
            this.requestLanguageCode = requestLanguageCode;
            this.resultLanguageCode = resultLanguageCode;
        }

        @Override
        public boolean equals(Object LanguageCodePair) {
            if (this == LanguageCodePair) return true;
            if (LanguageCodePair == null || getClass() != LanguageCodePair.getClass()) return false;
            LanguageCodePair that = (LanguageCodePair) LanguageCodePair;
            return Objects.equals(requestLanguageCode, that.requestLanguageCode) && Objects.equals(resultLanguageCode, that.resultLanguageCode);
        }

        @Override
        public int hashCode() {
            return Objects.hash(requestLanguageCode, resultLanguageCode);
        }
    }

    static {
        List<LanguageCodePair> supportedPairs = Arrays.asList(new LanguageCodePair("ko", "en"),
                new LanguageCodePair("ko", "zh"), new LanguageCodePair("ko", "vi"),
                new LanguageCodePair("ko", "th"), new LanguageCodePair("ko", "id"),
                new LanguageCodePair("ko", "fr"), new LanguageCodePair("ko", "es"),
                new LanguageCodePair("ko", "ru"), new LanguageCodePair("ko", "de"),
                new LanguageCodePair("ko", "it"), new LanguageCodePair("en", "ja"),
                new LanguageCodePair("en", "zh"), new LanguageCodePair("en", "vi"),
                new LanguageCodePair("en", "th"), new LanguageCodePair("en", "id"),
                new LanguageCodePair("en", "fr"));

        for (LanguageCodePair pair : supportedPairs) {
            languageSupportMap.put(pair, true);
            languageSupportMap.put(new LanguageCodePair(pair.getResultLanguageCode(), pair.getRequestLanguageCode()),
                    true); // 반대 관계 추가
        }
    }

    /**
     * 현재 번역 가능한 모든 언어들을 반환한다.
     *
     * @throws Exception 처리예정
     */
    @Transactional
    public List<LanguageDto> getAvailableLanguageList() {
        List<Language> languageList = languageRepository.findAll();
        List<LanguageDto> languageDtoList = new ArrayList<>();
        for (Language language : languageList) {
            languageDtoList.add(new LanguageDto(language));
        }
        return languageDtoList;
    }

    public static boolean isPapagoSupported(Translation translation) {
        if (translation.getRequestLanguage() == null) {
            throw new LanguageDetectionFailureException("언어 감지에 실패했습니다.");
        }
        return languageSupportMap.getOrDefault(new LanguageCodePair(translation.getRequestLanguage().getLanguageCode(),
                translation.getResultLanguage().getLanguageCode()), false);
    }

    @Transactional
    public Language detectLanguageType(RequestTranslationDto requestTranslationDto) {
        LanguageDetector detector = new OptimaizeLangDetector().loadModels();
        LanguageResult result = detector.detect(requestTranslationDto.getRequestText());
        return languageRepository.findByLanguageCode(result.getLanguage());
    }
}
