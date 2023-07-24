package com.hyundaiautoever.HEAT.v1.service;

import com.hyundaiautoever.HEAT.v1.dto.translation.RequestTranslationDto;
import com.hyundaiautoever.HEAT.v1.dto.translation.TranslationDto;
import com.hyundaiautoever.HEAT.v1.entity.Language;
import com.hyundaiautoever.HEAT.v1.entity.Translation;
import com.hyundaiautoever.HEAT.v1.entity.User;
import com.hyundaiautoever.HEAT.v1.repository.LanguageRepository;
import com.hyundaiautoever.HEAT.v1.repository.translation.TranslationRepository;
import com.hyundaiautoever.HEAT.v1.repository.user.UserRepository;
import com.hyundaiautoever.HEAT.v1.util.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TranslationServiceTest {

    @InjectMocks
    private TranslationService translationService;

    @Mock
    private LanguageService languageService;

    @Mock
    private PapagoService papagoService;

    @Mock
    private OpenAIService openAIService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private LanguageRepository languageRepository;

    @Mock
    private TranslationRepository translationRepository;


    private RequestTranslationDto requestTranslationDto;
    private Language requestLanguage;
    private Language resultLanguage;
    private User user1;
    private Translation translation;

    @BeforeEach
    void setUp() {
        user1 = User.builder()
                .userAccountNo(1L)
                .userEmail("user1@example.com")
                .passwordHash("password1")
                .userName("User1")
                .userRole(UserRole.user)
                .language(new Language())
                .signupDate(LocalDate.now())
                .build();

        requestLanguage = new Language();
        requestLanguage.setLanguageNo(2);
        requestLanguage.setLanguageCode("en");
        requestLanguage.setLanguageName("English");
        resultLanguage = new Language();
        resultLanguage.setLanguageNo(1);
        resultLanguage.setLanguageCode("ko");
        resultLanguage.setLanguageName("Korean");

        requestTranslationDto = new RequestTranslationDto();
        requestTranslationDto.setUserAccountNo(1L);
        requestTranslationDto.setRequestText("Hello");
        requestTranslationDto.setResultLanguageName("English");

        translation = Translation.builder()
                .translationNo(1L)
                .user(user1)
                .requestLanguage(requestLanguage)
                .resultLanguage(resultLanguage)
                .requestText("Hello")
                .resultText("안녕하세요")
                .build();
    }

    @DisplayName("requestTranslation 메소드 테스트")
    @Test
    void testRequestTranslationPapago() {
        try (MockedStatic<LanguageService> mocked = Mockito.mockStatic(LanguageService.class)) {
            mocked.when(() -> LanguageService.isPapagoSupported(any())).thenReturn(true);
            // Arrange
            Timestamp createDateTime = new Timestamp(System.currentTimeMillis());
            Translation translationWithoutResult = Translation.builder()
                    .translationNo(1L)
                    .requestLanguage(requestLanguage)
                    .resultLanguage(resultLanguage)
                    .createDatetime(createDateTime)
                    .user(user1)
                    .build();

            when(userRepository.findByUserAccountNo(1L)).thenReturn(Optional.of(user1));
            when(languageRepository.findByLanguageName(anyString())).thenReturn(requestLanguage);
            when(translationRepository.save(any())).thenReturn(translationWithoutResult);

            // Act
            Long translationNo = translationService.requestTranslation(requestTranslationDto);

            // Assert
            verify(papagoService, times(1)).getPapagoResponseAndSave(translationWithoutResult, requestTranslationDto);
        }
    }

    @DisplayName("requestTranslation 메소드 테스트")
    @Test
    void testRequestTranslationTestOpenAI() {
        try (MockedStatic<LanguageService> mocked = Mockito.mockStatic(LanguageService.class)) {
            mocked.when(() -> LanguageService.isPapagoSupported(any())).thenReturn(false);
            // Arrange
            Timestamp createDateTime = new Timestamp(System.currentTimeMillis());
            Translation translationWithoutResult = Translation.builder()
                    .translationNo(1L)
                    .requestLanguage(requestLanguage)
                    .resultLanguage(resultLanguage)
                    .createDatetime(createDateTime)
                    .user(user1)
                    .build();

            when(userRepository.findByUserAccountNo(1L)).thenReturn(Optional.of(user1));
            when(languageRepository.findByLanguageName(anyString())).thenReturn(requestLanguage);
            when(translationRepository.save(any())).thenReturn(translationWithoutResult);

            // Act
            Long translationNo = translationService.requestTranslation(requestTranslationDto);

            // Assert
            verify(openAIService, times(1)).getOpenAIResponseAndSave(translationWithoutResult, requestTranslationDto);
        }
    }

    @DisplayName("getTranslationResult 메소드 테스트")
    @Test
    void testGetTranslationResult() {
        //Arrange
        when(translationRepository.findByTranslationNo(1L)).thenReturn(Optional.of(translation));
        TranslationDto translationDto = new TranslationDto(translation);

        //Act
        TranslationDto result = translationService.getTranslationResult(1L);

        //Assert
        assertThat(result).isEqualTo(translationDto);
    }
}