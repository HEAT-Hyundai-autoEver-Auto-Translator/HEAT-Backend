package com.hyundaiautoever.HEAT.v1.service;

import com.hyundaiautoever.HEAT.v1.dto.language.LanguageDto;
import com.hyundaiautoever.HEAT.v1.dto.translation.RequestTranslationDto;
import com.hyundaiautoever.HEAT.v1.entity.Language;
import com.hyundaiautoever.HEAT.v1.repository.LanguageRepository;
import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LanguageServiceTest {

    @InjectMocks
    private LanguageService languageService;
    @Mock
    private LanguageRepository languageRepository;

    private Language language1;
    private Language language2;

    @BeforeEach
    public void setUp() {
        language1 = new Language();
        language1.setLanguageNo(1);
        language1.setLanguageName("Korean");
        language1.setLanguageCode("ko");

        language2 = new Language();
        language2.setLanguageNo(2);
        language2.setLanguageName("English");
        language2.setLanguageCode("en");
    }

    @DisplayName("getAvailableLanguageList 메소드 테스트")
    @Test
    public void testGetAvailableLanguageList() {
        //Arrange
        when(languageRepository.findAll()).thenReturn(Arrays.asList(language1, language2));
        List<LanguageDto> languageDtoList = Arrays.asList(new LanguageDto(language1), new LanguageDto(language2));

        //Act
        List<LanguageDto> result = languageService.getAvailableLanguageList();

        //Assert
        assertEquals(2, result.size());
    }

    @DisplayName("detectLanguageType 메소드 테스트")
    @Test
    public void testDetectLanguageType() {
        //Arrange
        RequestTranslationDto requestTranslationDto = new RequestTranslationDto();
        // requestTranslationDto에 필요한 데이터를 설정하세요.
        requestTranslationDto.setRequestText("안녕하세요");

        when(languageRepository.findByLanguageCode(anyString())).thenReturn(language1);

        //Act
        Language result = languageService.detectLanguageType(requestTranslationDto);

        //Assert
        assertEquals(language1, result);
    }
}