package com.hyundaiautoever.HEAT.v1.controller;

import com.hyundaiautoever.HEAT.v1.dto.RequestTranslationDto;
import com.hyundaiautoever.HEAT.v1.dto.TranslationDto;
import com.hyundaiautoever.HEAT.v1.exception.TranslationNotCompleteException;
import com.hyundaiautoever.HEAT.v1.service.TranslationService;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("api")
@Slf4j
public class TranslationController {

    private final TranslationService translationService;

    @PostMapping("/translation")
    public ResponseEntity<?> requestTranslation(@RequestBody RequestTranslationDto requestTranslationDto)
        throws IOException {
        Long translationNo = translationService.requestTranslation(requestTranslationDto);
        return new ResponseEntity<>(translationNo, HttpStatus.OK);
    }

    @GetMapping("/translation/translation-no/{translationNo}")
    public ResponseEntity getTranslationResult(@PathVariable Long translationNo) throws TranslationNotCompleteException {
        TranslationDto translationDto = translationService.getTranslationResult(translationNo);
        return new ResponseEntity<>(translationDto, HttpStatus.OK);
    }

    @GetMapping("/translation/user-id/{userId}")
    public ResponseEntity<?> findTranslationByUserId(@PathVariable String userId) throws IOException{
        return new ResponseEntity<>(translationService.findTranslationByUserId(userId), HttpStatus.OK);
    }

    @GetMapping("/translation/history")
    public ResponseEntity<?> findAllTranslation() {
        return new ResponseEntity<>(translationService.findAllTranslation(), HttpStatus.OK);
    }
}
