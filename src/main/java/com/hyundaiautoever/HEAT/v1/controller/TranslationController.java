package com.hyundaiautoever.HEAT.v1.controller;

import com.hyundaiautoever.HEAT.v1.dto.RequestTranslationDto;
import com.hyundaiautoever.HEAT.v1.dto.TranslationDto;
import com.hyundaiautoever.HEAT.v1.service.OpenAiResponse;
import com.hyundaiautoever.HEAT.v1.service.TranslationService;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.Mapping;
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

    @GetMapping("/translation")
    public TranslationDto getTranslationAPI(@RequestBody RequestTranslationDto requestTranslationDto)
        throws IOException {
        TranslationDto translationDto = translationService.getTranslationDto(requestTranslationDto);
        return translationDto;
    }

//    public List<TranslationDto> getUserTranslationHistory (@PathVariable Long userAccountNo){
//
//    }
}
