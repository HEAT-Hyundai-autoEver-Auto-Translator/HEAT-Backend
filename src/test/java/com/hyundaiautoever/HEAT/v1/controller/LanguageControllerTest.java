package com.hyundaiautoever.HEAT.v1.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hyundaiautoever.HEAT.v1.dto.language.LanguageDto;
import com.hyundaiautoever.HEAT.v1.entity.Language;
import com.hyundaiautoever.HEAT.v1.service.LanguageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class LanguageControllerTest {

    private MockMvc mockMvc;

    @Mock
    private LanguageService languageService;

    @InjectMocks
    private LanguageController languageController;

    private Language language;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(languageController).build();
        language = new Language();
        language.setLanguageNo(2);
        language.setLanguageCode("en");
        language.setLanguageName("English");
    }

    @Test
    public void getAvailableLanguageListTest() throws Exception {
        LanguageDto languageDto = new LanguageDto(language);

        List<LanguageDto> languageDtoList = Arrays.asList(languageDto);

        when(languageService.getAvailableLanguageList()).thenReturn(languageDtoList);

        mockMvc.perform(get("/api/language")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}
