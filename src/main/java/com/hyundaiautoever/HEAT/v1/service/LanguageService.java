package com.hyundaiautoever.HEAT.v1.service;

import com.hyundaiautoever.HEAT.v1.dto.language.LanguageDto;
import com.hyundaiautoever.HEAT.v1.dto.translation.RequestTranslationDto;
import com.hyundaiautoever.HEAT.v1.entity.Language;
import com.hyundaiautoever.HEAT.v1.entity.Translation;
import com.hyundaiautoever.HEAT.v1.repository.LanguageRepository;

import java.util.*;

import lombok.Getter;
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
public class LanguageService {

    private final LanguageRepository languageRepository;
    private final LanguageDetector detector = new OptimaizeLangDetector().loadModels();

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
                new LanguageCodePair("ko", "zh-CN"), new LanguageCodePair("ko", "vi"),
                new LanguageCodePair("ko", "th"), new LanguageCodePair("ko", "id"),
                new LanguageCodePair("ko", "fr"), new LanguageCodePair("ko", "es"),
                new LanguageCodePair("ko", "ru"), new LanguageCodePair("ko", "de"),
                new LanguageCodePair("ko", "it"), new LanguageCodePair("en", "ja"),
                new LanguageCodePair("en", "zh-CN"), new LanguageCodePair("en", "vi"),
                new LanguageCodePair("en", "th"), new LanguageCodePair("en", "id"),
                new LanguageCodePair("en", "fr"));

        for (LanguageCodePair pair : supportedPairs) {
            languageSupportMap.put(pair, true);
            languageSupportMap.put(new LanguageCodePair(pair.getResultLanguageCode(), pair.getRequestLanguageCode()),
                    true); // 반대 관계 추가
        }
    }

    /**
     * 현재 서비스에서 지원하는 언어 정보를 반환한다.
     *
     * @return 지원하는 언어 리스트
     **/
    @Transactional(readOnly = true)
    public List<LanguageDto> getAvailableLanguageList() {
        List<Language> languageList = languageRepository.findAll();
        List<LanguageDto> languageDtoList = new ArrayList<>();
        for (Language language : languageList) {
            languageDtoList.add(new LanguageDto(language));
        }
        return languageDtoList;
    }

    /**
     * 텍스트를 기반으로 언어의 종류를 감지한다.
     *
     * @param requestTranslationDto : 번역 요청 dto
     * @return 감지한 언어 기반 Language 객체
     **/
    @Transactional(readOnly = true)
    public Language detectLanguageType(RequestTranslationDto requestTranslationDto) {
        LanguageResult result = detector.detect(requestTranslationDto.getRequestText());
        String resultLanguageCode = result.getLanguage();
        //영국 영어 코드 "br" 예외 처리
        if ("br".equals(resultLanguageCode)) {
            resultLanguageCode = "en";
        }
        Language language = languageRepository.findByLanguageCode(resultLanguageCode);
        if (language == null) {
            language = languageRepository.findByLanguageCode("en");
        }
        return language;
    }

    /**
     * 요청언어 및 결광언어의 조합을 바탕으로 파파고 사용 가능 여부를 판단한다.
     *
     * @param translation
     * @return true : 파파고 지원 가능 / false : 파파고 지원 불가능
     **/
    public static boolean isPapagoSupported(Translation translation) {
        if (translation.getRequestLanguage() == null) {
            throw new RuntimeException("언어 감지에 실패했습니다.");
        }
        return languageSupportMap.getOrDefault(new LanguageCodePair(translation.getRequestLanguage().getLanguageCode(),
                translation.getResultLanguage().getLanguageCode()), false);
    }
}
