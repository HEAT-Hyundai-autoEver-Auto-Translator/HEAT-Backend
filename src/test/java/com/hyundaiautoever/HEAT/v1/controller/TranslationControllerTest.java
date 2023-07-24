package com.hyundaiautoever.HEAT.v1.controller;

import static org.junit.jupiter.api.Assertions.*;
import com.hyundaiautoever.HEAT.v1.dto.translation.RequestTranslationDto;
import com.hyundaiautoever.HEAT.v1.dto.translation.TranslationDto;
import com.hyundaiautoever.HEAT.v1.entity.Language;
import com.hyundaiautoever.HEAT.v1.entity.Translation;
import com.hyundaiautoever.HEAT.v1.entity.User;
import com.hyundaiautoever.HEAT.v1.service.TranslationService;

import com.hyundaiautoever.HEAT.v1.util.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.util.Arrays;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TranslationControllerTest {
    private MockMvc mockMvc;

    @Mock
    private TranslationService translationService;

    @InjectMocks
    private TranslationController translationController;

    private Translation translation;
    private User user;
    private RequestTranslationDto requestTranslationDto;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(translationController).build();

        user = User.builder()
                .userAccountNo(1L)
                .userEmail("user1@example.com")
                .passwordHash("password1")
                .userName("User1")
                .userRole(UserRole.user)
                .language(new Language())
                .signupDate(LocalDate.now())
                .build();

        translation = Translation.builder()
                .translationNo(1L)
                .user(user)
                .requestLanguage(new Language())
                .resultLanguage(new Language())
                .requestText("Hello")
                .resultText("안녕하세요")
                .build();

        requestTranslationDto = new RequestTranslationDto();
        requestTranslationDto.setRequestText("Test is so hard");
        requestTranslationDto.setResultLanguageName("Korean");
        requestTranslationDto.setUserAccountNo(1L);
    }

    @DisplayName("requestTranslation 메소드 테스트")
    @Test
    public void testRequestTranslation() throws Exception {
        //Act and Assert
        mockMvc.perform(post("/api/translation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(requestTranslationDto)))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @DisplayName("getTranslationResult 메소드 테스트")
    @Test
    public void testGetTranslationResult() throws Exception {
        //Arrange
        Long translationNo = 1L;
        //Act and Assert
        mockMvc.perform(get("/api/translation/translation-no")
                        .param("translation-no", translationNo.toString()))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @DisplayName("findTranslationByUserEmail 메소드 테스트")
    @Test
    public void testFindTranslationByUserEmail() throws Exception {
        //Arrange
        String userEmail = "test@user.com";
        //Act and Assert
        mockMvc.perform(get("/api/translation/user-email")
                        .param("user-email", userEmail))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @DisplayName("findAllTranslation 메소드 테스트")
    @Test
    public void testFindAllTranslation() throws Exception {
        //Act and Assert
        mockMvc.perform(get("/api/translation/history"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @DisplayName("deleteTranslationRecord 메소드 테스트")
    @Test
    public void testDeleteTranslationRecord() throws Exception {
        //Arrange
        Long translationNo = 1L;
        //Act and Assert
        mockMvc.perform(delete("/api/translation/translation-no")
                        .param("translation-no", translationNo.toString()))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }


}