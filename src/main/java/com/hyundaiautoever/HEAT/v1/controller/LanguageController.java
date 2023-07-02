package com.hyundaiautoever.HEAT.v1.controller;

import com.hyundaiautoever.HEAT.v1.dto.language.LanguageDto;
import com.hyundaiautoever.HEAT.v1.service.LanguageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
@Transactional
@Slf4j
public class LanguageController {

    private final LanguageService languageService;

    @GetMapping("/language")
    ResponseEntity<?> getAvailableLanguageList() {
        List<LanguageDto> availableLanguageDtoList = languageService.getAvailableLanguageList();
        return ResponseEntity.ok(availableLanguageDtoList);
    }
}
