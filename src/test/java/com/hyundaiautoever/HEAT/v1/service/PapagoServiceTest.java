package com.hyundaiautoever.HEAT.v1.service;

import com.hyundaiautoever.HEAT.v1.dto.translation.PapagoResponseDto;
import com.hyundaiautoever.HEAT.v1.dto.translation.RequestTranslationDto;
import com.hyundaiautoever.HEAT.v1.entity.Language;
import com.hyundaiautoever.HEAT.v1.entity.Translation;
import com.hyundaiautoever.HEAT.v1.entity.User;
import com.hyundaiautoever.HEAT.v1.repository.LanguageRepository;
import com.hyundaiautoever.HEAT.v1.repository.translation.TranslationRepository;
import com.hyundaiautoever.HEAT.v1.util.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PapagoServiceTest {

    @Mock
    private LanguageRepository languageRepository;
    @Mock
    private TranslationRepository translationRepository;
    @Mock
    private LanguageService languageService;
    @InjectMocks
    private PapagoService papagoService;

    private User user1;
    private Translation translation;
    private Language requestLanguage;
    private Language resultLanguage;

    @BeforeEach
    public void setup() {
        translation = translation = Translation.builder()
                .translationNo(1L)
                .user(user1)
                .requestLanguage(requestLanguage)
                .resultLanguage(resultLanguage)
                .requestText("Hello")
                .resultText("안녕하세요")
                .build();

        user1 = User.builder()
                .userAccountNo(1L)
                .userEmail("user1@example.com")
                .passwordHash("password1")
                .userName("User1")
                .userRole(UserRole.user)
                .profileImageUrl("old-image-url")
                .language(new Language())
                .signupDate(LocalDate.now())
                .build();

        requestLanguage = new Language();
        requestLanguage.setLanguageNo(1);
        requestLanguage.setLanguageCode("ko");
        requestLanguage.setLanguageName("Korean");
        resultLanguage = new Language();
        resultLanguage.setLanguageNo(2);
        resultLanguage.setLanguageCode("en");
        resultLanguage.setLanguageName("English");
    }

//    @Test
//    public void testGetPapagoResponseAndSave() {
//        //Arrange
//        RequestTranslationDto requestTranslationDto = new RequestTranslationDto();
//        // RequestTranslationDto 객체를 필요한 값으로 설정하세요.
//        requestTranslationDto.setRequestText("안녕하세요");
//        requestTranslationDto.setResultLanguageName("English");
//        requestTranslationDto.setUserAccountNo(1L);
//
//        PapagoResponseDto papagoResponseDto = PapagoResponseDto.builder()
//                .message(PapagoResponseDto.Message.builder()
//                        .result(PapagoResponseDto.Result.builder()
//                                .srcLangType("ko")
//                                .tarLangType("en")
//                                .translatedText("Hello")
//                                .build())
//                        .build())
//                .build();
//
//        when(languageService.detectLanguageType(requestTranslationDto)).thenReturn(requestLanguage);
//        when(languageRepository.findByLanguageName("English")).thenReturn(requestLanguage);
//
//        // Act
//        papagoService.getPapagoResponseAndSave(translation, requestTranslationDto);
//
//        // Assert
//        verify(papagoService, times(1)).getPapagoResponseDto(requestTranslationDto);
//        verify(papagoService, times(1)).saveCompleteResultTranslation(translation, papagoResponseDto);
//    }
}