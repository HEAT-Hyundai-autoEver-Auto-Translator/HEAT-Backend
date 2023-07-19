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

    // requestTranslation 테스트 코드
    @Test
    public void testRequestTranslation() throws Exception {

        mockMvc.perform(post("/api/translation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(requestTranslationDto)))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    // getTranslationResult 테스트 코드
    @Test
    public void testGetTranslationResult() throws Exception {
        Long translationNo = 1L;
        mockMvc.perform(get("/api/translation/translation-no")
                        .param("translation-no", translationNo.toString()))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    // findTranslationByUserEmail 테스트 코드
    @Test
    public void testFindTranslationByUserEmail() throws Exception {
        String userEmail = "test@user.com";
        mockMvc.perform(get("/api/translation/user-email")
                        .param("user-email", userEmail))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    // findAllTranslation 테스트 코드
    @Test
    public void testFindAllTranslation() throws Exception {
        mockMvc.perform(get("/api/translation/history"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    // deleteTranslationRecord 테스트 코드
    @Test
    public void testDeleteTranslationRecord() throws Exception {
        Long translationNo = 1L;

        mockMvc.perform(delete("/api/translation/translation-no")
                        .param("translation-no", translationNo.toString()))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }


}