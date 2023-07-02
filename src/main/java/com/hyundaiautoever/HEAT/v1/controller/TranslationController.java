package com.hyundaiautoever.HEAT.v1.controller;

import com.hyundaiautoever.HEAT.v1.dto.translation.RequestTranslationDto;
import com.hyundaiautoever.HEAT.v1.dto.translation.TranslationDto;
import com.hyundaiautoever.HEAT.v1.exception.TranslationNotCompleteException;
import com.hyundaiautoever.HEAT.v1.exception.TranslationNotFoundException;
import com.hyundaiautoever.HEAT.v1.service.TranslationService;

import java.util.List;
import java.util.Optional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Nullable;
import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("api")
@Slf4j
public class TranslationController {

    private final TranslationService translationService;

    @PostMapping("/translation")
    public ResponseEntity<?> requestTranslation(@RequestBody @Valid RequestTranslationDto requestTranslationDto) // Dto 네이밍
    {
        Long translationNo = translationService.requestTranslation(requestTranslationDto);
        return ResponseEntity.ok(translationNo);
    }

    @GetMapping("/translation/translation-no") // requestVariable
    public ResponseEntity getTranslationResult(
            @RequestParam(value = "translation-no") Long translationNo)
            throws TranslationNotFoundException, TranslationNotCompleteException {
        Optional<TranslationDto> translationDto = translationService.getTranslationResult(
                translationNo);
        return ResponseEntity.ok(translationDto);
    }

    @Nullable
    @GetMapping("/translation/user-email")
    public ResponseEntity<?> findTranslationByUserEmail(
            @RequestParam(value = "user-email") String userId) {
        List<TranslationDto> translationDtoList = translationService.findTranslationByUserEmail(
                userId);
        return ResponseEntity.ok(translationDtoList);
    }

    @Nullable
    @GetMapping("/translation/history")
    public ResponseEntity<?> findAllTranslation() {
        List<TranslationDto> translationDtoList = translationService.findAllTranslation();
        return ResponseEntity.ok(translationDtoList);
    }
}
